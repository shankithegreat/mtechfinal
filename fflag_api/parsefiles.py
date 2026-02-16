import os
import re
import http.client
import json


# Example usage
# root_path = 'C:/Users/srafwea/Documents/cxp-address-services-main'
# target_folder = 'constants'
# input_values = ['moversPhaseThreeQualFlag']


def list_files_recursively(startpath):
    for root, dirs, files in os.walk(startpath):
        level = root.replace(startpath, "").count(os.sep)
        indent = " " * 4 * level
        print(f"{indent}{os.path.basename(root)}/")

        subindent = " " * 4 * (level + 1)
        for f in files:
            print(f"{subindent}{f}")


def find_folder(root_path, target_folder):
    for dirpath, dirnames, filenames in os.walk(root_path):
        if target_folder in dirnames:
            folder_path = os.path.join(dirpath, target_folder)
            print(f"Found {target_folder} folder at: {folder_path}")
            find_constants_java_files(folder_path)
            return folder_path

    print(f"{target_folder} folder not found within {root_path}")
    return None


def find_constants_java_files(target_path):
    global constant_files

    for dirpath, dirnames, filenames in os.walk(target_path):
        for f in filenames:
            if os.path.splitext(f)[1].lower() == ".java":
                full_path = os.path.join(dirpath, f)
                print(full_path)
                constant_files.append(full_path)


def extract_java_methods(filepath):
    with open(filepath, 'r', encoding='utf-8') as fr:
        data = fr.read()

    methods = []

    regex = (
        r'((public|private|protected|static)\s+)*'
        r'([\w\[\]]+)\s+'
        r'(\w+)\s*'
        r'\(([^\)]*)\)\s*'
        r'\{([\s\S]*?)\}'
    )

    for match in re.findall(regex, data, re.DOTALL):
        method_signature = match[0]
        method_name = match[3]
        method_code = match[5]

        methods.append({
            'signature': method_signature,
            'name': method_name,
            'code': method_code
        })

    return methods


def parse_constants_from_code(code):
    pattern = r'public\s+static\s+final\s+String\s+(\w+)\s*=\s*"([^"]+)"'
    matches = re.findall(pattern, code)

    # Map value → constant name
    constants_map = {value: name for name, value in matches}
    return constants_map


def find_constant_names_from_code(code, values):
    constants_map = parse_constants_from_code(code)

    result = {}
    for value in values:
        result[value] = constants_map.get(value, "Not Found")

    return result


def scan_code_for_flag(flag_name, code_file_path):
    if 'MappingUtil.java' in code_file_path:
        print(code_file_path)

        method_list = extract_java_methods(code_file_path)
        for method_data in method_list:
            print(method_data['signature'])


# -------------------- MAIN FLOW --------------------

constant_files = []
all_code_files = []

root_path = "C:/Users/srafwea/Documents/cxp-address-services-main"
target_folder = "constants"
input_values = ["moversPhaseThreeQualFlag"]

find_folder(root_path, target_folder)

for const_file in constant_files:
    with open(const_file, 'r', encoding='utf-8') as f:
        data = f.read()

    result = find_constant_names_from_code(data, input_values)

    for value, variable in result.items():
        print(value, "=>", variable)

skip_dirs = ['.git', '__pycache__', 'test']

for root, dirs, files in os.walk(root_path):
    if os.path.basename(root) in skip_dirs:
        dirs[:] = []
        continue

    if 'util' in root.lower():
        for f in files:
            if os.path.splitext(f)[1].lower() == ".java":
                all_code_files.append(os.path.join(root, f))

print(len(all_code_files))

for code_file_path in all_code_files:
    scan_code_for_flag(variable, code_file_path)
