#!/usr/bin/env python3
"""Compact view of the IO schedule → raceId mapping, for human verification.
Consecutive slots on the same (day, track) that map to the same raceId get
collapsed into a single row showing the time range."""
import json, sys, urllib.request
from collections import defaultdict, OrderedDict

sys.path.insert(0, '/tmp')
from match_races import fetch_schedule, fetch_races, parse_race, match

DAY_ORDER = {'Wednesday': 0, 'Thursday': 1, 'Friday': 2, 'Saturday': 3, 'Sunday': 4}
TRACK_ORDER = [
    'main_stage', 'world_cup_1', 'world_cup_2', 'all_skills',
    'spec', 'whoopville', 'gq_rookie', 'tiny_trainier',
]

def main():
    schedule = fetch_schedule()
    raw_races = fetch_races()
    races = [parse_race(r) for r in raw_races]
    race_name = {r['raceId']: r['raceName'] for r in raw_races}

    sessions = [s for s in schedule['sessions'] if s.get('activity')]
    session_to_raceid = {s['id']: match(s, races)[0] for s in sessions}

    days = sorted({s['day'] for s in sessions}, key=lambda d: DAY_ORDER.get(d, 99))
    total_rows = 0

    for day in days:
        print(f'\n═══ {day} ═══')
        tracks_on_day = sorted(
            {s['trackId'] for s in sessions if s['day'] == day},
            key=lambda t: TRACK_ORDER.index(t) if t in TRACK_ORDER else 99
        )
        for tid in tracks_on_day:
            slots = sorted(
                [s for s in sessions if s['day'] == day and s['trackId'] == tid],
                key=lambda s: s['startTime']
            )
            # Compress runs of identical raceId
            i = 0
            while i < len(slots):
                rid = session_to_raceid[slots[i]['id']]
                j = i
                while j + 1 < len(slots) and session_to_raceid[slots[j + 1]['id']] == rid:
                    j += 1
                run = slots[i:j + 1]
                start = run[0]['startTime']
                end = run[-1]['endTime']
                activities = list(OrderedDict.fromkeys(s['activity'] for s in run))
                act_str = ' / '.join(activities)
                rid_str = rid or '  —  '
                rname = race_name.get(rid, '') if rid else '(unmapped)'
                spans = f'×{len(run)}' if len(run) > 1 else ''
                print(f'  {tid:14s} {start}–{end} {spans:3s}  {rid_str:6s}  {act_str}'
                      + (f'  →  {rname}' if rid else '  →  (unmapped)'))
                total_rows += 1
                i = j + 1

    print(f'\n// Compacted from 264 schedule slots into {total_rows} race rows')


if __name__ == '__main__':
    main()
