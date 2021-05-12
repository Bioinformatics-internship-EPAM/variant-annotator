from utils import RESOURCE_FOLDER, HOST, run_command

URL = "{}/{}".format(HOST, "vcfs")
DB_NAME = "db_test_variant-annotator"


# TODO: Add cleanup after this test
# Now works because of recreating DB on each test run
def test_post_vcfs_success():
    headers = 'content-type: multipart/form-data'
    file = "{}/{}".format(RESOURCE_FOLDER, "test_vcf_file.vcf")
    print(
        "curl -w \"%{{http_code}}\" -X POST '{}?db_name={}' -H {} -F vcf_file=@{}".format(URL, DB_NAME, headers, file))
    out, err = run_command(
        "curl -w \"%{{http_code}}\" -X POST '{}?db_name={}' -H \'{}\' -F vcf_file=@{}".format(URL, DB_NAME, headers,
                                                                                              file))
    assert int(out) == 200, (out, err)
