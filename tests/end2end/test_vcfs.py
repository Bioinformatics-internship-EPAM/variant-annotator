from utils import RESOURCE_FOLDER, HOST, run_command, get_cookies

URL = "{}/{}".format(HOST, "vcfs")
DB_NAME = "db_test_variant-annotator"


# TODO: Add cleanup after this test
# Now works because of recreating DB on each test run
def test_post_vcfs_success():
    def convert_cookies(cookies):
        return ["{}={}".format(key, value) for key, value in cookies.items()]

    cookies = convert_cookies(get_cookies())
    headers = 'content-type: multipart/form-data'
    file = "{}/{}".format(RESOURCE_FOLDER, "test_vcf_file.vcf")
    command = "curl -w \"%{{http_code}}\" -X POST -b {} '{}?db_name={}' -H \'{}\' -F vcf_file=@{}".format(cookies[0],
                                                                                                          URL,
                                                                                                          DB_NAME,
                                                                                                          headers,
                                                                                                          file)
    out, err = run_command(command)
    assert int(out) == 200, {"OUT: ": out, "ERROR: ": err, "COMMAND: ": command}
