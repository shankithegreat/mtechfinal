import http.client
import json

from methodparser import MethodParser


class RemoveFFlagVariableImp:

    def process_variable_removal(self, file_method_dict):
        modified_methods = []
        method_parser = MethodParser()

        for dict_item in list(file_method_dict.items()):
            file_path = dict_item[0]
            method_list = dict_item[1]

            for method_detail in method_list:
                print(method_detail.get("name"))

                updated_code = self.invoke_vegas_temp_to_remove_flag(
                    method_detail.get("signature"),
                    method_detail.get("variableused")
                )

                try:
                    tmp_final_code = json.loads(updated_code)["prediction"]
                    tmp_final_code = tmp_final_code.replace("java", "")
                    method_detail["signature"] = tmp_final_code
                    modified_methods.append(method_detail)
                except Exception as e:
                    print(e)

            self.update_java_file(file_path, modified_methods)

        return ""

    def update_java_file(self, filepath, modified_methods):
        with open(filepath, "r", encoding="utf-8") as f:
            data = f.read()

        # Apply modifications in reverse order to avoid shifting indices
        for method in sorted(
            modified_methods,
            key=lambda x: x["start"],
            reverse=True
        ):
            data = (
                data[:method["start"]] +
                method["signature"] +
                data[method["end"]:]
            )

        with open(filepath, "w", encoding="utf-8") as f:
            f.write(data)

    def invoke_vegas_temp_to_remove_flag(self, code, flagname):
        prompt_output = self.createPrompt_df(code, flagname)

        conn = http.client.HTTPSConnection("jarvisuat.ebiz.verizon.com")

        payload = json.dumps({
            "useCase": "SDLC_AUTOMATION",
            "contextId": "SDLC_US",
            "pre_seeded_Prompt": prompt_output,
            "parameters": {
                "temperature": 0.7,
                "maxOutputTokens": 2048,
                "topp": 1
            },
            "dynamic_params": {},
            "safetySettings": [
                {"category": "HARM_CATEGORY_HATE_SPEECH", "threshold": "OFF"},
                {"category": "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold": "OFF"},
                {"category": "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold": "OFF"},
                {"category": "HARM_CATEGORY_HARASSMENT", "threshold": "OFF"}
            ]
        })

        headers = {
            "Content-Type": "application/json"
        }

        conn.request(
            method="POST",
            url="/vegas/api/invoke",
            body=payload,
            headers=headers
        )

        response = conn.getresponse()
        data = response.read()
        return data.decode("utf-8")

    def createPrompt_df(self, code, flagname):
        context_prompt = (
            "\n\n\nRemove the implementation of "
            + flagname +
            " variable and of its usage in the above code. "
            "DO NOT ADD ANY EXTRA TEXT AND RETURN IN A PLAIN TEXT.\n\n\n"
            + code
        )

        print(f"Prompt:\n{context_prompt}")
        return context_prompt
