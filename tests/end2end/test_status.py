#!/usr/nin/python
import requests

from utils import HOST


def test_status():
    response = requests.get("{}/{}".format(HOST, "status"))
    assert response.status_code == 200
    body = response.json()
    assert body.get("status") == "ok"
