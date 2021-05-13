import os
import subprocess

import requests

HOST = "http://localhost:8080/api"
TEST_FOLDER = os.path.dirname(os.path.realpath(__file__))
RESOURCE_FOLDER = "{}/{}".format(TEST_FOLDER, "resources")
WAIT_PERIOD = 30


def get_current_folder(file):
    return os.path.dirname(os.path.realpath(file))


def run_command(command, stdout=subprocess.PIPE):
    p = subprocess.Popen(command, stdout=stdout, stderr=subprocess.PIPE, shell=True)
    return p.communicate()


def get_cookies():
    data = {
        'username': 'admin',  # TODO: get fro config
        'password': '123'
    }

    response = requests.post("http://localhost:8080/api/login", data=data, allow_redirects=False)
    return response.cookies.get_dict()
