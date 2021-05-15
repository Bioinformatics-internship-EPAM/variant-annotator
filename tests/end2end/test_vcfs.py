import pytest
import requests
import vcf

from utils import RESOURCE_FOLDER, BASE_URI, run_command, get_cookies

DB_NAME = "db_test_variant-annotator"


def _read_vcf(filepath):
    # type: (str) -> list
    reader = vcf.Reader(open(filepath, "r"))
    return [record for record in reader]


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
    vcf_data = _read_vcf("{}/test_vcf_file.vcf".format(RESOURCE_FOLDER))
    data = {
        "variants": [{
            "pos": int(vcf_data[0].POS),
            "chrom": str(vcf_data[0].CHROM),
            "ref": str(vcf_data[0].REF),
            "alt": str(vcf_data[0].ALT[0])
        }]
    }
    info = {}
    for key, value in vcf_data[0].INFO.items():
        info[key] = float(value[0])

    expected_data = {
        "variants": [
            {
                "pos": int(vcf_data[0].POS),
                "chrom": str(vcf_data[0].CHROM),
                "ref": str(vcf_data[0].REF),
                "alt": str(vcf_data[0].ALT[0]),
                "annotations": [
                    {
                        "info": info,
                        "dbName": DB_NAME
                    }]
            }
        ]
    }

    response = _request_getAnnotatedVariants(data)
    assert response.status_code // 100 == 2, (response.status_code, response.json())
    response_body = response.json()
    assert len(response_body["variants"]) == 1
    r_info = {}
    for key, value in response_body['variants'][0]['annotations'][0]['info'].items():
        r_info[key] = float(value)
    response_body['variants'][0]['annotations'][0]['info'] = r_info
    assert response_body == expected_data
