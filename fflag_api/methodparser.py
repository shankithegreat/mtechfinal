import re
import javalang


class MethodParser:

    def extract_methods(self, filepath):
        """Read and parse the Java file into a javalang AST."""
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()

        tree = javalang.parse.parse(content)
        source_lines = content.splitlines(keepends=True)

        methods = self.extract_all_methods(tree)

        # methods.sort(key=lambda m: m.position.line if m.position else 0, reverse=True)
        return methods, source_lines

    def extract_all_methods(self, tree):
        """
        Traverse the AST and return all MethodDeclaration nodes.
        """
        seen = set()
        methods = []

        for method in self.filter_nodes(tree, javalang.tree.MethodDeclaration):
            if method.body and method.position:
                unique_id = (method.position.line, method.name)
                if unique_id in seen:
                    continue

                seen.add(unique_id)
                methods.append(method)

        return methods

    def extract_methods_with_constant(self, tree, constant):
        """
        Traverse the AST and return MethodDeclaration nodes
        where the constant is used.
        """
        methods_with_constant = []

        for method in self.filter_nodes(tree, javalang.tree.MethodDeclaration):
            if method.body:
                body_str = "\n".join(str(statement) for statement in method.body)
                if constant in body_str:
                    methods_with_constant.append(method)

        return methods_with_constant

    def filter_nodes(self, node, target_type):
        """
        Recursively traverse the AST node and yield all nodes
        that are instances of target_type.
        """
        if isinstance(node, target_type):
            yield node

        for attr_name in dir(node):
            # Skip private attributes or metadata
            if attr_name.startswith("_"):
                continue

            attr = getattr(node, attr_name)

            if isinstance(attr, list):
                for item in attr:
                    if isinstance(item, javalang.ast.Node):
                        yield from self.filter_nodes(item, target_type)

            elif isinstance(attr, javalang.ast.Node):
                yield from self.filter_nodes(attr, target_type)

    def modify_methods(self, methods):
        modified = []

        for method in methods:
            code = method["code"]
            name = method["name"]

            # Example modification: append a comment to method body
            modified_code = re.sub(
                r"(\{)", r"\1\n    // Modified by script", code, 1
            )

            modified.append({
                "start": method["start"],
                "end": method["end"],
                "code": modified_code
            })

        return modified
