#!/usr/nin/python
import requests

from utils import BASE_URI, get_cookies


def test_status():
    cookies = get_cookies()
    print(cookies)
    response = requests.get("{}/{}".format(BASE_URI, "status"), cookies=cookies)
    assert response.status_code == 200
    body = response.json()
    assert body.get("status") == "ok"
