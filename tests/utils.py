import os
import subprocess

import requests
import yaml

TEST_FOLDER = os.path.dirname(os.path.realpath(__file__))
RESOURCE_FOLDER = "{}/{}".format(TEST_FOLDER, "resources")


def read_yaml(filename):
    with open(filename, "r") as file:
        return yaml.load(file, yaml.FullLoader)


__CONFIG = read_yaml("{}/config.yaml".format(RESOURCE_FOLDER))
BASE_URI = "{}://{}:{}{}".format(__CONFIG["schema"], __CONFIG["host"], __CONFIG["port"], __CONFIG["path"])
USERNAME = __CONFIG["username"]
PASSWORD = __CONFIG["password"]


def get_current_folder(file):
    return os.path.dirname(os.path.realpath(file))


def run_command(command, stdout=subprocess.PIPE):
    process = subprocess.Popen(command, stdout=stdout, stderr=subprocess.PIPE, shell=True)
    return process.communicate()


def get_cookies():
    data = {
        'username': USERNAME,
        'password': PASSWORD
    }

    response = requests.post("http://localhost:8080/api/login", data=data, allow_redirects=False)
    return response.cookies.get_dict()
