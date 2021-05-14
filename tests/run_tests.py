#!/usr/bin/python3

import time

import pytest

from utils import TEST_FOLDER, run_command

WAIT_PERIOD = 30

out, err = run_command("cd {} && cd .. && docker-compose up --build -d".format(TEST_FOLDER))
if err:
    print(err)
print("Started dockers")
print("Wait {} second".format(WAIT_PERIOD))
time.sleep(WAIT_PERIOD)
pytest.main(["{}/{}".format(TEST_FOLDER, "end2end")])
run_command("cd {} && cd .. && docker-compose down".format(TEST_FOLDER))
