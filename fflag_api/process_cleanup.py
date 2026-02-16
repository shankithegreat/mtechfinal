import os
import re
import http.client
import json
import configparser
from time import sleep
from mistralai import Mistral
import javalang

from methodparser import MethodParser
from remove_flag_varible_imp import RemoveFFlagVariableImp

api_key = "sHqd9rqG8XRsc8ga2SnXQpjMrJlbMKgb"
model = "mistral-large-latest"
constants_target_folder = "constants"
current_working_directory = os.getcwd()
client = Mistral(api_key=api_key)
# Read config
config = configparser.RawConfigParser()
config_file_path = os.path.join(current_working_directory, "config", "fflag.config")
config.read(config_file_path)

REPO_FOLDER_PATH = config.get("flagmapping", "repo_folder_path")
PROMPT_FILE_PATH = config.get("fflagsection", "prompt_file_path")
SERVICE_FILE_RELATIVE_PATH = config.get("flagmapping", "service_file_relative_path")
OUTPUT_FILE_PATH = config.get("flagmapping", "output_file_path")


class ProcessCleanup:

    def get_impacted_methods(self, flagname, repo_name):
        repo_path = os.path.join(REPO_FOLDER_PATH, repo_name)
        constant_files = self.find_folder(repo_path, constants_target_folder)
        
        for const_file in constant_files:
            with open(const_file, "r", encoding="utf-8") as f:
                data = f.read()
            
            result = self.find_constant_names_from_code(data, flagname)
            
            for value, variable in result.items():
                if variable != "Not Found":
                    code_file_path = os.path.join(repo_path, SERVICE_FILE_RELATIVE_PATH)
                    method_list, _, _, _ = self.scan_code_for_flag(variable, code_file_path)
                    
                    impacted_methods = []
                    for method_dict in method_list:
                        impacted_methods.append({
                            'name': method_dict['name'],
                            'code': method_dict['signature']
                        })
                    return impacted_methods
        return []

    def process_deprecated_flag_cleanup(self, flagname, repolist):
        for repo_folder_path in repolist:
            repo_path = os.path.join(REPO_FOLDER_PATH, repo_folder_path)
            print(repo_path)

            constant_files = self.find_folder(repo_path, constants_target_folder)
            self.process_constant_files(constant_files, repo_path, flagname)

        return ""

    def find_folder(self, root_path, target_folder):
        constant_files = []

        for dirpath, dirnames, filenames in os.walk(root_path):
            if target_folder in dirnames:
                folder_path = os.path.join(dirpath, target_folder)
                print(f"Found '{target_folder}' folder at: {folder_path}")

                tmp_const_file_path = self.find_constants_files(folder_path)
                if tmp_const_file_path is not None:
                    constant_files.append(tmp_const_file_path)
                return constant_files

        print(f"'{target_folder}' folder not found within '{root_path}'")
        return constant_files

    def find_constants_files(self, targetpath):
        for dirpath, dirnames, filenames in os.walk(targetpath):
            for f in filenames:
                if os.path.splitext(f)[1].lower() == ".java":
                    print(os.path.join(dirpath, f))
                    return os.path.join(dirpath, f)
        return None

    def process_constant_files(self, constant_files, repo_path, flagname):
        all_code_files = []

        for const_file in constant_files:
            with open(const_file, "r", encoding="utf-8") as f:
                data = f.read()

            result = self.find_constant_names_from_code(data, flagname)

            for value, variable in result.items():
                skip_dirs = [".git", "__pycache__", "test"]

                for root, dirs, files in os.walk(repo_path):
                    if os.path.basename(root) in skip_dirs:
                        dirs[:] = []
                        continue

                    if "util" in root.lower():
                        for f in files:
                            if os.path.splitext(f)[1].lower() == ".java":
                                all_code_files.append(os.path.join(root, f))

                print(len(all_code_files))

                code_file_path = os.path.join(repo_path, SERVICE_FILE_RELATIVE_PATH)

                _, method_list, source_lines, methodname_variable_map = (
                    self.scan_code_for_flag(variable, code_file_path)
                )

                if method_list:
                    method_list.sort(
                        key=lambda m: m.position.line if m.position else 0,
                        reverse=True
                    )

                    for method in method_list:
                        source_lines = self.process_method_with_api(
                            source_lines, method, methodname_variable_map
                        )

                    sleep(3)

                    try:
                        with open(
                            OUTPUT_FILE_PATH,
                            "w",
                            encoding="utf-8",
                        ) as f:
                            f.writelines(source_lines)
                        print("success")
                    except Exception:
                        print("Error writing")
                        return False

        return True

    def process_method_with_api(self, source_lines, method_node, methodname_variable_map):
        start_line = method_node.position.line - 1 if method_node.position else None
        if start_line is None:
            return source_lines

        brace_count = 0
        method_started = False
        method_end = start_line

        for i in range(start_line, len(source_lines)):
            line = source_lines[i]

            if not method_started and "{" in line:
                method_started = True

            if method_started:
                brace_count += line.count("{") - line.count("}")

            if method_started and brace_count == 0:
                method_end = i
                break

        method_code = "".join(source_lines[start_line:method_end + 1])
        print(f"Sending method lines {start_line + 1}-{method_end + 1} to API")

        var_name_to_remove = None
        for method_map in methodname_variable_map:
            if method_map["methodName"] == method_node.name:
                var_name_to_remove = method_map["varname"]
                break

        modresp = self.invoke_vegas_temp_to_remove_flag(method_code, var_name_to_remove)

        try:            
            modified_code = modresp
        except Exception as e:
            print(e)
            modified_code = method_code

        new_code_lines = modified_code.splitlines(keepends=True)
        source_lines = (
            source_lines[:start_line] +
            new_code_lines +
            source_lines[method_end + 1:]
        )

        return source_lines

    def createPrompt_df(self, code, flagname):
        with open(
            PROMPT_FILE_PATH,
            "r",
            encoding="utf-8",
        ) as f:
            context_prompt = f.read()

        context_prompt = (
            context_prompt
            .replace("@variable", flagname)
            .replace("@code", code)
        )

        print(context_prompt)
        return context_prompt

    def invoke_vegas_temp_to_remove_flag(self, code, flagname):
        prompt_output = self.createPrompt_df(code, flagname)

        try:
            print("inside mistral call")
            chat_response = client.chat.complete(
                model = model,
                messages = [
                    {
                        "role": "user",
                        "content": prompt_output,
                    },
                ]
            )
            res = chat_response.choices[0].message.content
            return res
        except Exception as e:
            print(f"Error: {e}")

    def parse_constants_from_code(self, code):
        pattern = r'public\s+static\s+final\s+String\s+(\w+)\s*=\s*"([^"]+)"'
        matches = re.findall(pattern, code)
        return {value: name for name, value in matches}

    def find_constant_names_from_code(self, code, value):
        constants_map = self.parse_constants_from_code(code)
        return {value: constants_map.get(value, "Not Found")}

    def scan_code_for_flag(self, flagname, code_file_path):
        method_parser = MethodParser()

        if os.path.basename(code_file_path) != os.path.basename(SERVICE_FILE_RELATIVE_PATH):
            return None, None, None

        print(code_file_path)

        method_list, source_lines = method_parser.extract_methods(code_file_path)

        method_for_modifications = []
        impacted_methods = []
        methodname_variable_map = []
        pattern = r"FeatureFlagReader\.isFeatureEnabled\((.*?)\)" 
        for method in method_list:
            if method.body:
                body_str = "\n".join(str(s) for s in method.body)
                if flagname in body_str:
                    tmp_code = self.get_method_code(method, source_lines)
                    start_line = method.position.line - 1 if method.position else None
                    if start_line is None:
                        continue
                    brace_count = 0
                    method_started = False
                    end_line = start_line
                    for i in range(start_line, len(source_lines)):
                        line = source_lines[i]
                        if not method_started and "{" in line:
                            method_started = True
                        if method_started:
                            brace_count += line.count("{") - line.count("}")
                        if method_started and brace_count == 0:
                            end_line = i
                            break
                    for line in tmp_code.splitlines():
                        if flagname in line: # and "=" in line:
                            match = re.search(pattern, tmp_code)
                            varname =  ''
                            if match:
                                varname = match.group(1)

                            method_detail = {
                                'name': method.name,
                                'signature': tmp_code,
                                'variableused': varname,
                                'start': start_line + 1,
                                'end': end_line + 1
                            }

                            methodname_variable_map.append({
                                "methodName": method.name,
                                "varname": varname
                            })
                            method_for_modifications.append(method_detail)
                            impacted_methods.append(method)
                            break

        return method_for_modifications, impacted_methods, source_lines, methodname_variable_map

    def get_method_code(self, method_node, source_lines):
        start_line = method_node.position.line - 1 if method_node.position else None
        if start_line is None:
            return ""

        brace_count = 0
        method_started = False
        method_end = start_line

        for i in range(start_line, len(source_lines)):
            line = source_lines[i]
            if not method_started and "{" in line:
                method_started = True

            if method_started:
                brace_count += line.count("{") - line.count("}")

            if method_started and brace_count == 0:
                method_end = i
                break

        return "".join(source_lines[start_line:method_end + 1])
