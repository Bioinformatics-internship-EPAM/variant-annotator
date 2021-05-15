#!/usr/bin/python3
import argparse
import time

import pytest

from utils import TEST_FOLDER, run_command

WAIT_PERIOD = 15
parser = argparse.ArgumentParser(description='Run end2end tests')
parser.add_argument('--no_build', action='store_true', help="Do not rebuild containers")

args = parser.parse_args()

BUILD_FLAG = "--build"
if args.no_build:
    BUILD_FLAG = ""

out, err = run_command("cd {} && cd .. && docker-compose up {} -d".format(TEST_FOLDER, BUILD_FLAG))
if err:
    print(err)
print("Started dockers")
print("Wait {} second".format(WAIT_PERIOD))
time.sleep(WAIT_PERIOD)
pytest.main(["{}/{}".format(TEST_FOLDER, "end2end")])
run_command("cd {} && cd .. && docker-compose down".format(TEST_FOLDER))
