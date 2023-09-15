# Crowdin Project Progress SVG Generator

Call the [Crowdin API](https://developer.crowdin.com/api/v2/) to get the project progress and generate the SVG.

[Create Crowdin access token](https://crowdin.com/settings#api-key)

# Usage

## Install requirements
```shell
pip install -r requirements.txt
```

## Run
```shell
python start.py crowdin_api_token
```

## Output
![](crowdin_project_progress.svg)

# Crowdin API

* **[List Supported Languages](https://developer.crowdin.com/api/v2/#operation/api.languages.getMany)**
* **[Get Project Progress](https://developer.crowdin.com/api/v2/#operation/api.projects.languages.progress.getMany)**