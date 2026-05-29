# IO 2026 Schedule API — Handover Notes

If you've inherited this and have no idea what it is, read this top to
bottom once. The other file in this folder, **[`README.md`](README.md)**,
is the day-to-day operational reference (deploy steps, refresh
commands) — come back to it after you've read this one.

---

## What this is, in one paragraph

A Google Apps Script that reads the **IO 2026 schedule Google Sheet**
and exposes it as a public JSON HTTP endpoint. The iOS and Android
RaceSync apps fetch from that endpoint to render the IO schedule tab.
The script also injects a hand-curated `raceId` onto each session in
the response so a tap in the mobile schedule can deep-link straight to
the corresponding **multigp.com** race detail page.

That's it. Three moving parts: a Sheet, a Script, an API.

---

## Why it exists at all

The IO event schedule has historically been maintained in **one Google
Sheet** by the events team. That sheet is also **embedded on
multigp.com** (the public IO 2026 page renders it directly). Whatever
changes the team makes in the sheet shows up on the website
immediately, no redeploys.

When we built the IO tab in the mobile apps, we needed:

1. The **same schedule data** the website uses (single source of truth — no
   parallel spreadsheet to drift out of sync).
2. **A way to serve it as JSON** the apps can fetch — Google Sheets has
   built-in CSV export but it's awkward to consume from a mobile app.
3. **Race IDs** alongside the activity names, so tapping "Team Race
   Qualifying" on Wed 10 AM in the app would jump to the right
   multigp.com race detail page.

The script solves all three:

- It reads the sheet → cleaner contract than parsing CSV exports.
- It outputs JSON → easy to consume from iOS/Android.
- It joins in a race-ID lookup that we manage **outside** the sheet, so
  we don't pollute the embed-on-website data with mobile-only metadata.

That last point is the "hack" the user described. See below.

---

## The architecture in five boxes

```
┌─────────────────────────┐
│  Google Sheet           │   ← Events team edits here. Source of truth for the schedule.
│  (IO 2026 schedule)     │
└─────────┬───────────────┘
          │  bound Apps Script project reads via SpreadsheetApp
          ▼
┌─────────────────────────┐
│  Code.gs                │   ← This file. Parses the sheet rows into JSON.
│  (in Apps Script editor)│      Holds the SESSION_RACE_IDS lookup map.
└─────────┬───────────────┘
          │  deployed as a Web app → fixed /exec URL
          ▼
┌─────────────────────────┐
│  HTTPS JSON endpoint    │   ← The "API". Same URL forever (deployment-locked).
│  script.google.com/...  │
└────┬────────────────┬───┘
     │                │
     ▼                ▼
┌──────────┐   ┌──────────┐
│  iOS app │   │ Android  │   ← Both apps fetch on cold launch, cache to disk,
│          │   │   app    │      refresh in background. Display schedule + race links.
└──────────┘   └──────────┘
```

URLs and identifiers live in the **README's "Source of truth"** table — go
there for clickable links. Don't memorize them; they're written down.

---

## The race-ID hack (this is the part to read carefully)

### The problem

A session in the sheet is a cell at the intersection of a **track**
column (Main Stage, World Cup 1, Spec, etc.) and a **time slot** row
(10 AM, 11 AM, …). The cell content is the activity name — "Team Race
Qualifying", "Open Fly", etc.

Each race on multigp.com has a unique **race ID** (integer, looks like
`32075`). We want every session in the mobile app's schedule to deep-link
to its corresponding race detail page on multigp.com.

The "obvious" way would be to **add a column to the sheet** with the
race ID. We didn't do that. Three reasons:

1. **The sheet is rendered on the public website.** Adding a column of
   integer IDs would either show up on the website (ugly) or require
   the website's embed to be reworked (out of scope and risky).
2. **The events team owns the sheet.** Asking them to also maintain
   race IDs in a parallel column means more places to forget to update.
3. **Race IDs come from a different system.** They live on multigp.com,
   which the events team doesn't directly edit.

### The solution

We hard-code the entire `sessionId → raceId` map **inside the Apps
Script itself**, as a JS object literal:

```js
const SESSION_RACE_IDS = {
  'wednesday-1000-main_stage': '32075',   // Team Race Qualifying
  'wednesday-1100-main_stage': '32075',
  'wednesday-1200-main_stage': '32075',
  'wednesday-1300-main_stage': '32075',
  'wednesday-1400-main_stage': '32076',   // Team Race Final
  ...
};
```

Session IDs are derived deterministically from `(day, time, track)`, so
they're stable as long as the sheet's structure doesn't change.

When the script emits its JSON, it adds a `raceId` field to each
session by looking it up in this map:

```js
sessions.push({
  id: id,
  // ... activity, time, track, etc. ...
  raceId: SESSION_RACE_IDS[id] || null,
});
```

Sessions without a mapping (Movie Night, "Kung Fu Fightn", contingency
"World Cup Rain Day", closed slots) get `raceId: null`. The mobile apps
treat null-raceId sessions as non-tappable.

### Why we call it a "hack"

- The map duplicates information that *could* live in the sheet,
  splitting maintenance between two places.
- Editing the script requires a redeploy (sheet edits don't).
- The map's keys are tightly coupled to the sheet's row/column
  structure — if someone renames "Main Stage" to "MainStage" in the
  sheet, every `main_stage` key in the map becomes stale.
- Keeping the map in sync with multigp.com requires the matcher script
  (next section) — it's a multi-step manual job, not automated.

It's a *pragmatic* hack — it ships, it works, the alternatives are
worse — but if someone later wants to fold these IDs back into the
sheet (without polluting the website embed), that's a reasonable
follow-up.

---

## The Python matcher (so the map isn't hand-typed)

Hand-typing 258 entries against multigp.com would be miserable and
error-prone. So there's a helper:

- **`match_races.py`** — fetches the live schedule JSON *and* the live
  list of races from the MultiGP-International-Open chapter page on
  multigp.com, then auto-matches each session to its race via track-
  prefix + keyword scoring (with typo tolerance for upstream wording
  glitches like "Burn Motors" / "Global Qualifer"). Outputs the entire
  `SESSION_RACE_IDS` block ready to paste into `Code.gs`.

- **`compact_view.py`** — verification helper that compresses the 264
  schedule slots into ~83 race-row groups by day/track, so you can
  eyeball whether the matcher's results look sane before deploying.

- **`build_code_gs.py`** — glues the static script template together
  with the latest `SESSION_RACE_IDS` block from `match_races.py` and
  spits out a complete `Code.gs` you can paste into the Apps Script
  editor.

Currently matches **258 / 264** sessions. The 6 it can't match are
intentional (no corresponding multigp.com race exists for things like
"Movie Night").

The actual commands live in [`README.md`](README.md).

---

## What lives where (mental model)

| Thing | Lives in | Who edits | When it changes |
|---|---|---|---|
| The schedule (days, times, tracks, activities) | The Google Sheet | Events team | Whenever the IO schedule changes |
| The JSON parsing / formatting logic | `Code.gs` (Apps Script editor) | Dev | Rarely — only if schema changes |
| The `sessionId → raceId` map | `Code.gs`, hardcoded as `SESSION_RACE_IDS` | Dev (via matcher) | Whenever races are added/renamed on multigp.com |
| The deployed API URL | Apps Script's "Manage deployments" | Dev | **Should never change** — apps are pinned to it |
| The version of `Code.gs` in this repo | `tools/io-schedule-apps-script/Code.gs` | Dev | Whenever the deployed version changes |

The repo-versioned `Code.gs` and the deployed `Code.gs` should always
match. The repo version is the source of truth for code review and
history; the deployed version is what's running. When you change one,
update the other immediately.

---

## How to make changes (the three common cases)

**1. The events team edited the schedule (added/removed/renamed a session).**
Nothing to do. Sheet edits are picked up live — the next mobile-app
fetch sees them. If a session was renamed or a new time slot was added,
the matcher's keyword logic will probably still hit; if not, the
`raceId` on the affected sessions will go `null`. Run
`compact_view.py` and look for new `(unmapped)` rows.

**2. multigp.com has new races, or race IDs changed.**
Run the matcher to regenerate `SESSION_RACE_IDS`:
```sh
python3 match_races.py > /tmp/session_race_ids.js
python3 compact_view.py | less    # eyeball the diff
python3 build_code_gs.py          # writes the new Code.gs
```
Paste the new `Code.gs` into the Apps Script editor → Save → **Manage
deployments → edit existing → New version → Deploy**. The URL stays
the same. Commit the updated `Code.gs` to the repo.

**3. The JSON contract needs a new field.**
This is a coordinated change — bump the Apps Script (add the field
to the session shape), redeploy, then update the iOS and Android
models (both already tolerate unknown fields, so old app versions
won't break). Both apps use Gson / ObjectMapper which silently ignore
unknown fields, so the API can grow additively without breaking
shipped clients.

---

## Common failure modes (and what to do)

| Symptom | Likely cause | Fix |
|---|---|---|
| Mobile apps show "couldn't load IO schedule" | Sheet structure changed; script parsing failed | Look at the script's `Logger.log` output (`testBuildSchedule` function in Apps Script editor). Probably a header row moved or a column was inserted. Adjust parsing in `buildSchedule()`. |
| Schedule loads, but some sessions show `raceId: null` when they shouldn't | New activity name doesn't match any race in the matcher's keyword rules, or activity name changed on multigp.com | Re-run `match_races.py` and inspect the unmapped rows in `compact_view.py`. Add a normalization entry in `HINT_NORMALIZE` or `ACTIVITY_NORMALIZE` in `match_races.py` to teach it the new wording, then regenerate. |
| Tapping a session on Android does nothing | Session has `raceId: null` (intentional, e.g. Movie Night) or the race ID points to a race that no longer exists on multigp.com | Re-run the matcher. |
| The Apps Script URL stopped working | Someone made a *new* deployment instead of updating the existing one — the URL changes per deployment | In Apps Script editor → **Manage deployments**, find the deployment the mobile apps point at, edit it (don't create a new one). If a new deployment was made and is the only one with the latest code, you're stuck either:<br>1. **Update the mobile apps** to point at the new URL (requires app store releases — slow)<br>2. **Re-deploy the latest code** to the original deployment (recommended) |
| `match_races.py` fails | `multigp.com` chapter page changed, or the schedule API URL changed | Update the constants at the top of `match_races.py`. |

---

## What access you need if you're inheriting this

- **Edit access to the IO 2026 schedule Google Sheet** (so you can open the bound Apps Script editor)
- **Deploy access on the Apps Script project** (so you can ship new versions of `Code.gs`)
- **A multigp.com admin account** (only if you need to look up race IDs by hand for spot-checks; the matcher handles the bulk job)
- **Write access to this repo** (to keep the versioned `Code.gs` in sync)

There's nothing else — no separate hosting, no CI/CD, no DB, no secrets
to rotate. The whole pipeline runs on Google's free Apps Script tier
plus the matcher scripts on your laptop.

---

## Provenance

- **Built**: May 2026, in the run-up to IO 2026 (event runs Jun 10–14
  at AMA Grounds, Muncie, Indiana).
- **Original author**: Viki "Barracuda" Baarathi (Android lead).
- **iOS counterpart**: lives in the `racesync-ios` repo, branch
  `release-v2.1`. iOS hits the same endpoint with its own Swift model
  layer (ObjectMapper).
- **Android consumer**: this repo, paths
  `domain/.../model/io/Event.kt`,
  `data/.../repository/IoScheduleRepositoryImpl.kt`,
  `app/.../screens/io/IoScheduleScreen.kt`,
  `app/.../viewmodels/IoScheduleViewModel.kt`.

If everything in this folder ever gets deleted accidentally, the
`Code.gs` running on Google's servers is the actual source of truth
for the deployed behavior — you can pull it back into the repo by
copy-pasting from the Apps Script editor.

---

## TL;DR

- Google Sheet → Apps Script reads it → JSON API → mobile apps fetch.
- We hardcode `sessionId → raceId` in the script (not the sheet)
  because the sheet is shared with the public website.
- The Python tools auto-generate that hardcoded map by cross-referencing
  the live multigp.com chapter race list.
- Don't change the deployed URL. Update the existing deployment in
  place.
- For day-to-day commands, see [`README.md`](README.md).
