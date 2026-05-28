#!/usr/bin/env python3
"""Auto-match IO 2026 schedule sessions to MultiGP race IDs."""
import json, re, sys, urllib.request
from collections import defaultdict

SCHEDULE_URL = 'https://script.google.com/macros/s/AKfycbwxgL-ib1uq1EMyfkjrpvmdoMSxzKGG5x--MV4GAMExkM3UEV5FHovTM_UKbTtALQBj/exec'
RACES_URL = 'https://www.multigp.com/MultiGP/request/handleRequest.php'

# Maps race-name prefix → schedule trackId
TRACK_PREFIXES = {
    'Main Stage':  'main_stage',
    'WorldCup1':   'world_cup_1',
    'WorldCup2':   'world_cup_2',
    'AllSkills':   'all_skills',
    'GQTrack':     'gq_rookie',
    'SpecTrack':   'spec',
    'TinyTrainer': 'tiny_trainier',
    'Whoop':       'whoopville',
}

# Races without a "<Track>-" prefix: explicit track + hint
NO_PREFIX_RACES = {
    'World Cup Finals': ('main_stage', 'World Cup Finals'),
}

# Rewrite the race-name HINT to match schedule-activity vocabulary.
# Keep "Almost WC Final" as-is — expanding it pollutes the WC Qualifying match.
HINT_NORMALIZE = [
    ('Just Fly',               'Open Fly'),
    ('Over 40',                'Masters'),
    ('Over 50',                'Vintage'),
    ('ProSpec-8',              'Pro Spec Groups of 8'),
    ('FreedomSpec-8',          'Freedom Spec Groups of 8'),
    ('Pro Spec Battle',        'Pro Spec Battle Royal'),
    ('Qualifying-and-Finals',  'Whoop Qualifying Finals'),
    ('Hammers Race1',          'Hammers'),
    ('Chapter Organizer Race', 'Chapter Organizer'),
    ('Qualifier2',             'Second Qualifier'),  # disambiguate from the Wed Global Qualifier
]

# Schedule typo / wording fixes applied before tokenizing the schedule activity.
ACTIVITY_NORMALIZE = [
    ('Global Qualifer',        'Global Qualifier'),  # upstream typo
    ('Burn Motors',            'Burnt Motors'),      # upstream typo
    ('Individuals',            'Individual'),        # stemming nudge
]

# Tokenizer stop-words (high-frequency noise that would inflate match scores)
STOP = {
    'race', 'racing', 'the', 'a', 'an', 'of', 'for', 'on', 'and', 'in', 'to',
    'by', 'with', 'as', 'is', 'pilots', 'pilot', 'quad', 'quads',
}

MONTH_TO_DAY = {  # Jun N → schedule day name
    10: 'Wednesday', 11: 'Thursday', 12: 'Friday', 13: 'Saturday', 14: 'Sunday',
}

DAY_INDEX = {'Wednesday': 0, 'Thursday': 1, 'Friday': 2, 'Saturday': 3, 'Sunday': 4}


def fetch_schedule():
    with urllib.request.urlopen(SCHEDULE_URL) as r:
        return json.load(r)


def fetch_races():
    req = urllib.request.Request(
        RACES_URL,
        data=json.dumps({'function': 'getUpcomingEvents', 'data': {'chapter': 1442}}).encode(),
        headers={'Content-Type': 'application/json', 'User-Agent': 'Mozilla/5.0'},
    )
    with urllib.request.urlopen(req) as r:
        return json.load(r)['data']['results']


def tokenize(s):
    return {w for w in re.split(r'[^a-z0-9]+', s.lower()) if w and w not in STOP}


def parse_race(race):
    """→ (trackId, day, hour, minute, hint_tokens, race_id, race_name)"""
    name = race['raceName']
    if name in NO_PREFIX_RACES:
        track, hint = NO_PREFIX_RACES[name]
    else:
        parts = name.split('-', 1)
        prefix = parts[0]
        track = TRACK_PREFIXES.get(prefix)
        hint = parts[1] if len(parts) > 1 else name
    for old, new in HINT_NORMALIZE:
        if old in hint:
            hint = hint.replace(old, new)

    # Parse "Jun 10, 4:00PM"
    m = re.match(r'\w+\s+(\d+),\s+(\d+):(\d+)(AM|PM)', race['raceStartDate'])
    if not m:
        day, hour, mins = None, 0, 0
    else:
        day = MONTH_TO_DAY.get(int(m.group(1)))
        hour = int(m.group(2))
        mins = int(m.group(3))
        if m.group(4) == 'PM' and hour != 12: hour += 12
        if m.group(4) == 'AM' and hour == 12: hour = 0

    return {
        'track': track,
        'day': day,
        'hour': hour,
        'mins': mins,
        'tokens': tokenize(hint),
        'id': race['raceId'],
        'name': name,
    }


def session_hour(session):
    h, _ = session['startTime'].split(':')
    return int(h)


def match(session, races):
    """→ (raceId, note) — raceId is None if no confident match."""
    if not session.get('activity'):
        return None, 'closed'

    track_id = session['trackId']
    candidates = [r for r in races if r['track'] == track_id]
    if not candidates:
        return None, f'no race on track {track_id}'

    # Single candidate on this track — always wins regardless of day/time.
    if len(candidates) == 1:
        return candidates[0]['id'], candidates[0]['name']

    activity = session['activity']
    for old, new in ACTIVITY_NORMALIZE:
        activity = activity.replace(old, new)
    s_tokens = tokenize(activity)
    if not s_tokens:
        return None, 'empty activity tokens'

    scored = [(len(s_tokens & c['tokens']), c) for c in candidates]
    max_score = max(s for s, _ in scored)
    if max_score == 0:
        return None, 'no keyword match'

    top = [c for s, c in scored if s == max_score]
    if len(top) == 1:
        return top[0]['id'], top[0]['name']

    # Tied — try same-day first, then fall back to nearest-day.
    same_day = [c for c in top if c['day'] == session['day']]
    if len(same_day) == 1:
        return same_day[0]['id'], same_day[0]['name']
    if len(same_day) >= 2:
        s_hour = session_hour(session)
        same_day.sort(key=lambda c: abs(c['hour'] - s_hour))
        if same_day[0]['hour'] != same_day[1]['hour']:
            return same_day[0]['id'], same_day[0]['name']
        ids = ', '.join(c['id'] for c in same_day)
        return None, f'ambiguous same-day: {ids}'

    # No same-day candidate — pick the race whose day is closest to the session day.
    s_day_idx = DAY_INDEX.get(session['day'], 99)
    top.sort(key=lambda c: abs(DAY_INDEX.get(c['day'], 99) - s_day_idx))
    if len(top) == 1 or (abs(DAY_INDEX.get(top[0]['day'], 99) - s_day_idx)
                         < abs(DAY_INDEX.get(top[1]['day'], 99) - s_day_idx)):
        return top[0]['id'], top[0]['name']
    ids = ', '.join(c['id'] for c in top)
    return None, f'ambiguous across days: {ids}'


def main():
    schedule = fetch_schedule()
    raw_races = fetch_races()
    races = [parse_race(r) for r in raw_races]
    parsed_ok = sum(1 for r in races if r['track'])
    print(f'// Races fetched: {len(races)}, with-track: {parsed_ok}', file=sys.stderr)

    # Group sessions by day for readable output
    sessions = [s for s in schedule['sessions'] if s.get('activity')]
    print(f'// Sessions with activity: {len(sessions)}', file=sys.stderr)

    day_order = {'Wednesday': 0, 'Thursday': 1, 'Friday': 2, 'Saturday': 3, 'Sunday': 4}
    by_day = defaultdict(list)
    for s in sessions:
        by_day[s['day']].append(s)

    matched = 0
    print('const SESSION_RACE_IDS = {')
    for day in sorted(by_day, key=lambda d: day_order.get(d, 99)):
        print(f'  // ──────── {day} ────────')
        for s in sorted(by_day[day], key=lambda s: (s['startTime'], s['trackId'])):
            race_id, note = match(s, races)
            key = f"'{s['id']}'".ljust(46)
            if race_id:
                matched += 1
                value = f"'{race_id}'"
            else:
                value = 'null'
            print(f"  {key}: {value:<10}, // {s['activity']}")
        print()
    print('};')

    pct = 100.0 * matched / len(sessions)
    print(f'\n// MATCHED: {matched} / {len(sessions)} sessions ({pct:.0f}%)', file=sys.stderr)


if __name__ == '__main__':
    main()
