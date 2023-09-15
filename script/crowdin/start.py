#!/usr/bin/python
# -*- coding: UTF-8 -*-

import svgwrite
import sys
from crowdin_api import CrowdinClient

project_id = 597587
token = sys.argv[1]


# https://developer.crowdin.com/api/v2/
def get_project_progress():
    client = CrowdinClient(token=token)
    languages = client.languages.list_supported_languages(limit=500)['data']
    language_pairs = {}
    for language in languages:
        id = language['data']['id']
        name = language['data']['name']
        language_pairs[id] = name

    languages = client.translation_status.get_project_progress(project_id)['data']
    for language in languages:
        id = language['data']['languageId']
        name = language_pairs.get(id)
        if name == None:
            name = id
        language['data']['name'] = name
    languages = list(map(lambda l: l['data'], languages))
    languages = sorted(languages, key=lambda l: l['name'])
    return languages


line_height = 26
svg_width = 500


def append_language_group(dwg, language, index):
    label_width = 200
    progress_width = 160
    insert = 10

    g = dwg.add(dwg.g(class_="font12", transform='translate(0,{})'.format(index * line_height)))
    g.add(dwg.text(language['name'], insert=(label_width, 18), style='text-anchor:end;'))

    translation_progress = language['translationProgress'] / 100.0
    approval_progress = language['approvalProgress'] / 100.0

    progress_insert = (label_width + insert, 11.4)
    if translation_progress < 100:
        g.add(dwg.rect(insert=progress_insert, size=(progress_width, 6), rx=3, ry=3, fill='#999',
                       style='filter:opacity(30%);'))
    if translation_progress > 0 and approval_progress < 100:
        g.add(dwg.rect(insert=progress_insert, size=(progress_width * translation_progress, 6),
                       rx=3, ry=3, fill='#5D89C3'))
    if approval_progress > 0:
        g.add(dwg.rect(insert=progress_insert, size=(progress_width * approval_progress, 6),
                       rx=3, ry=3, fill='#71C277'))

    g.add(dwg.text('{}%'.format(language['translationProgress']),
                   insert=(progress_insert[0] + progress_width + insert, 18)))
    return g


languages = get_project_progress()
print(languages)

dwg = svgwrite.Drawing('crowdin_project_progress.svg',
                       size=(svg_width, len(languages) * line_height))
# load web font by CSS @import
dwg.embed_stylesheet("""
@import url(https://fonts.googleapis.com/css?family=Noto+Sans);
.font12 {
    font-family: "Noto Sans";
    font-size: 12px;
    fill: #999;
}
""")
for i in range(0, len(languages)):
    append_language_group(dwg, languages[i], i)

dwg.save(pretty=True)
