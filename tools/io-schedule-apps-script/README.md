# IO 2026 Schedule — Apps Script & Mapping Tools

Backend tooling for the MultiGP IO 2026 schedule API consumed by the iOS
and Android RaceSync apps. Stored here so changes are reviewable and
self-contained.

**These files live outside the Gradle build** — the Android project
(`:app`, `:domain`, `:data`) never sees them. They won't impact compile,
APK packaging, lint, or proguard. The folder is purely repo-side tooling.

## Source of truth

| | |
|---|---|
| **Backing sheet** | https://docs.google.com/spreadsheets/d/1V61KB5MwUdui7bAajiiCMGHGtbch2cyxIRJbjeDT1EY/edit |
| **Deployed endpoint** | `https://script.google.com/macros/s/AKfycbwxgL-ib1uq1EMyfkjrpvmdoMSxzKGG5x--MV4GAMExkM3UEV5FHovTM_UKbTtALQBj/exec` |
| **Chapter race list** | https://www.multigp.com/chapters/view/?chapter=MultiGP-International-Open (chapter id `1442`) |

The endpoint URL is fixed — mobile clients are pinned to it. Always
re-deploy under the **same deployment** (Manage deployments → edit
existing → New version → Deploy) to keep the URL stable.

## Files

| File | Purpose |
|---|---|
| `Code.gs` | The Google Apps Script. Paste the whole file into the Apps Script editor of the backing sheet and redeploy. Contains the schedule-parsing logic, the `SESSION_RACE_IDS` lookup map, and the `doGet` web-app entry point. |
| `match_races.py` | Auto-matches the 264 active schedule sessions to the ~55 multigp.com races for the IO chapter. Outputs the `SESSION_RACE_IDS` block as JS. |
| `compact_view.py` | Verification helper. Compresses the 264 slots into ~83 race rows grouped by day and track, with each merged time-range showing the matched race. |
| `build_code_gs.py` | Glues the static script template together with the latest `SESSION_RACE_IDS` block from `match_races.py`. Writes the result to `Code.gs`. |

## API contract — what changed

The response now includes a `raceId` field on every session:

```json
{
  "id": "wednesday-1000-main_stage",
  "day": "Wednesday",
  "date": "2026-06-10",
  "startTime": "10:00",
  "endTime": "11:00",
  "trackId": "main_stage",
  "activity": "Team Race Qualifying",
  "status": "scheduled",
  "raceId": "32075"
}
```

`raceId` is a **nullable string**. It's `null` when:

- The session has no activity (closed time block).
- The activity has no corresponding MultiGP race (e.g. "Movie Night",
  "Kung Fu Fightn", "World Cup Rain Day" contingency).
- The matcher couldn't disambiguate between multiple candidate races.

Both iOS and Android clients tolerate `raceId` being absent or `null`
(ObjectMapper / Gson silently ignore unknown / null fields), so this is
a strictly additive change. Existing installs keep working.

## Updating `SESSION_RACE_IDS` after multigp.com races change

When races are added, renamed, or removed on multigp.com, refresh the
map and redeploy:

```sh
cd tools/io-schedule-apps-script

# 1. Re-fetch + re-match (fetches live schedule + live races)
python3 match_races.py > /tmp/session_race_ids.js

# 2. Eyeball the diff
python3 compact_view.py | less

# 3. Rebuild Code.gs with the new map embedded
python3 build_code_gs.py

# 4. Open Code.gs, copy the whole file, paste into the Apps Script editor
#    (https://script.google.com/), Save, then
#    Deploy → Manage deployments → edit existing → New version → Deploy
```

Sheet edits are picked up live — no redeploy needed unless you change
`Code.gs` or `SESSION_RACE_IDS`.

## Matching strategy (what `match_races.py` does)

For each schedule session with a non-null activity:

1. Filter candidate races to those whose name starts with the session's
   track prefix (`Main Stage-` → `main_stage`, `WorldCup1-` →
   `world_cup_1`, etc.).
2. If only one candidate exists on that track, it wins (e.g. the only
   `Whoop-Qualifying-and-Finals` covers every Whoop schedule slot).
3. Otherwise, tokenize and score by keyword overlap between the
   schedule's activity and the race's hint, after applying typo /
   wording normalizations (`Burn Motors` ↔ `Burnt Motors`,
   `Global Qualifer` ↔ `Global Qualifier`, `Just Fly` ↔ `Open Fly`).
4. On ties, prefer the candidate whose day matches the session's day;
   if none match exactly, prefer the candidate on the closest day.

Tested against the full live payload — currently matches **258 / 264
sessions** (98%). The remaining 6 are intentionally unmapped (no
corresponding race exists on multigp.com).

## Adding / fixing mappings manually

If the matcher produces a wrong or null result for a session, just edit
the `SESSION_RACE_IDS` block in `Code.gs` directly. The matcher is a
convenience for bulk regeneration — it's not the source of truth.
