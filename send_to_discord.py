import requests
import os
from requests.auth import HTTPBasicAuth

def send_issue_update_to_discord(issue_id_or_key):
    JIRA_URL = os.getenv("JIRA_URL")
    JIRA_EMAIL = os.getenv("JIRA_EMAIL")
    JIRA_API_TOKEN = os.getenv("JIRA_API_TOKEN")
    DISCORD_WEBHOOK_URL = os.getenv("DISCORD_WEBHOOK_URL")

    jira_api_url = f"{JIRA_URL}/rest/api/3/issue/{issue_id_or_key}?expand=changelog"

    response = requests.get(jira_api_url, auth=HTTPBasicAuth(JIRA_EMAIL, JIRA_API_TOKEN))

    if response.status_code == 200:
        issue_data = response.json()
    else:
        print("Jira API 요청 실패:", response.status_code, response.text)
        return

    changelog = issue_data.get("changelog", {}).get("histories", [])
    previous_status, current_status = None, None

    for history in changelog:
        for item in history.get("items", []):
            if item["field"] == "status":
                previous_status = item["fromString"]
                current_status = item["toString"]
                break
        if previous_status and current_status:
            break

    if previous_status and current_status:
        message = {
            "content": f"**이슈 상태가 변경되었습니다**: {previous_status} -> {current_status}\n\n" +
                       f"**이슈 제목**: {issue_data['fields']['summary']}\n" +
                       f"**이슈 유형**: {issue_data['fields']['issuetype']['name']}\n" +
                       f"**담당자**: {issue_data['fields']['assignee']['displayName'] if issue_data['fields']['assignee'] else '미할당'}"
        }
        discord_response = requests.post(DISCORD_WEBHOOK_URL, json=message)
        if discord_response.status_code == 204:
            print("Discord 메시지 전송 성공")
        else:
            print("Discord 메시지 전송 실패:", discord_response.status_code, discord_response.text)
    else:
        print("상태 변경 정보를 찾을 수 없습니다.")
