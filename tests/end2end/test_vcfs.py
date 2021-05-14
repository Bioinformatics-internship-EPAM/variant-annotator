import allel
import pytest
import requests

from utils import RESOURCE_FOLDER, BASE_URI, run_command, get_cookies

DB_NAME = "db_test_variant-annotator"


def _request_getAnnotatedVariants(data):
    url = "{}/getAnnotatedVariants".format(BASE_URI)
    return requests.post(url=url, cookies=get_cookies(), json=data, headers={"Content-type": "application/json"})


@pytest.mark.first
def test_post_vcfs_success():
    def convert_cookies(cookies):
        return ["{}={}".format(key, value) for key, value in cookies.items()]

    url = "{}/{}".format(BASE_URI, "vcfs")
    cookies = convert_cookies(get_cookies())
    headers = 'content-type: multipart/form-data'
    file = "{}/{}".format(RESOURCE_FOLDER, "test_vcf_file.vcf")
    command = "curl -w \"%{{http_code}}\" -X POST -b {} '{}?db_name={}' -H \'{}\' -F vcf_file=@{}".format(cookies[0],
                                                                                                          url,
                                                                                                          DB_NAME,
                                                                                                          headers,
                                                                                                          file)
    out, err = run_command(command)
    assert int(out) == 200, {"OUT: ": out, "ERROR: ": err, "COMMAND: ": command}


def test_post_getAnnotatedVariants_empty():
    data = {
        "variants": []
    }
    response = _request_getAnnotatedVariants(data)
    assert response.status_code // 100 == 2, (response.status_code, response.json())
    assert response.json() == data


def test_post_getAnnotatedVariants():
    vcf_data = allel.read_vcf("{}/test_vcf_file.vcf".format(RESOURCE_FOLDER))
    data = {
        "variants": [{
            "pos": int(vcf_data["variants/POS"][0]),
            "chrom": vcf_data["variants/CHROM"][0],
            "ref": vcf_data["variants/REF"][0],
            "alt": vcf_data["variants/ALT"][0][0]
        }]
    }

    response = _request_getAnnotatedVariants(data)
    assert response.status_code // 100 == 2, (response.status_code, response.json())
