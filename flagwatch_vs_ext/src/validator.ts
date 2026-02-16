import * as vscode from 'vscode';
import * as parser from '@babel/parser';
import traverse from '@babel/traverse';

export class FlagValidator {

    private diagnosticCollection: vscode.DiagnosticCollection;

    constructor() {
        this.diagnosticCollection = vscode.languages.createDiagnosticCollection('flagwatch');
    }

    public validateDocument(document: vscode.TextDocument) {
        if (document.languageId !== 'typescript' && document.languageId !== 'javascript') {
            return;
        }

        const diagnostics: vscode.Diagnostic[] = [];
        const content = document.getText();

        try {
            const ast = parser.parse(content, {
                sourceType: 'module',
                plugins: ['typescript', 'jsx', 'classProperties']
            });

            traverse(ast, {
                // Rule 1: Detect string literals that look like flags
                StringLiteral(path) {
                    const value = path.node.value;
                    if (value.startsWith('FEATURE_') || value.startsWith('FLAG_')) {
                        // Check if it's being assigned to a variable named the same (definition) - skip
                        // Check if it's inside a comparison or function call argument (usage) - flag it

                        // Heuristic: If we see a string literal "FEATURE_X", it's likely a magic string usage.
                        // We want users to use constants: MyFlags.FEATURE_X

                        const diagnostic = new vscode.Diagnostic(
                            new vscode.Range(
                                document.positionAt(path.node.start || 0),
                                document.positionAt(path.node.end || 0)
                            ),
                            `FlagWatch: Avoid using string literal "${value}" for feature flags. Define a constant instead.`,
                            vscode.DiagnosticSeverity.Warning
                        );
                        diagnostics.push(diagnostic);
                    }
                },

                // Rule 2: Check for Error Handling
                // We want to see checks like `if (FEATURE_X)` or access to FEATURE_X wrapped in try/catch if it involves dangerous logic.
                // This is hard to statically analyze perfectly. 
                // Let's implement a simpler version: If a flag is used in a function call, ensure it's in a try/catch block.
                Identifier(path) {
                    const name = path.node.name;
                    if (name.startsWith('FEATURE_') || name.startsWith('FLAG_')) {
                        // Check if parent is a function call or something executable
                        // For simplicity, let's say any usage of the identifier.

                        // Walk up the tree to find a TryStatement
                        let output: any = path.findParent((p) => p.isTryStatement());

                        // If not found, warn
                        if (!output) {
                            const diagnostic = new vscode.Diagnostic(
                                new vscode.Range(
                                    document.positionAt(path.node.start || 0),
                                    document.positionAt(path.node.end || 0)
                                ),
                                `FlagWatch: Feature flag logic "${name}" is not wrapped in a try/catch block. Risk of unhandled errors.`,
                                vscode.DiagnosticSeverity.Warning
                            );
                            // Only add if we haven't already flagged this location (unlikely for Identifiers)
                            diagnostics.push(diagnostic);
                        }
                    }
                }
            });

        } catch (e) {
            // Parser error, ignore
            console.error(e);
        }

        this.diagnosticCollection.set(document.uri, diagnostics);
    }

    public dispose() {
        this.diagnosticCollection.dispose();
    }
}
