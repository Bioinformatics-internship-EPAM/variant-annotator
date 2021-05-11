#!/usr/nin/python
import requests

HOST = "http://localhost:8080/api"


def test_status():
    response = requests.get("{}/{}".format(HOST, "status"))
    assert response.status_code == 200
    body = response.json()
    assert body.get("status") == "ok"
