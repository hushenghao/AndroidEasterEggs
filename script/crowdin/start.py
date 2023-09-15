#!/usr/bin/python
# -*- coding: UTF-8 -*-

import json
import sys

import svgwrite
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
    return languages


line_height = 26
svg_width = 500

def create_svg_group(dwg,language,index):
    label_width = 200
    progress_width = 160
    insert = 10

    g = dwg.g(transform = 'translate(0,{})'.format(index * line_height))
    g.add(dwg.text(language['data']['name'],insert=(label_width,18),fill='#999',font_size='12',style='text-anchor:end;'))

    translation_progress = language['data']['translationProgress'] / 100.0
    approval_progress = language['data']['approvalProgress'] / 100.0

    progress_insert = (label_width + insert, 11.6)
    if translation_progress < 100:
        g.add(dwg.rect(insert=progress_insert,size=(progress_width, 6),rx=3,ry=3,fill='#F5F5F5'))
    if translation_progress > 0 and approval_progress < 100:
        g.add(dwg.rect(insert=progress_insert,size=(progress_width * translation_progress, 6),rx=3,ry=3,fill='#5D89C3'))
    if approval_progress > 0:
        g.add(dwg.rect(insert=progress_insert,size=(progress_width * approval_progress, 6),rx=3,ry=3,fill='#71C277'))

    g.add(dwg.text('{}%'.format(language['data']['translationProgress']),insert=(progress_insert[0] + progress_width + insert,18),fill='#999',font_size='10'))
    return g

languages = get_project_progress()
print(languages)

dwg = svgwrite.Drawing('crowdin_project_progress.svg',size=(svg_width,len(languages) * line_height))
for i in range(0,len(languages)):
    dwg.add(create_svg_group(dwg,languages[i],i))

dwg.save(pretty=True)
