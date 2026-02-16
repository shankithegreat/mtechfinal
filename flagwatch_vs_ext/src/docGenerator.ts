import * as vscode from 'vscode'
import * as fs from 'fs';
import * as path from 'path';
import * as parser from '@babel/parser';
import traverse from '@babel/traverse';

/**
 * Structure of the generated feature flags documentation.
 */
interface FeatureFlagDoc {
    flags: {
        name: string;
        defaultValue: any;
        files: string[]; // Files where this flag is used
    }[];
}

export class DocGenerator {

    private workspaceRoot: string | undefined;

    constructor() {
        this.workspaceRoot = vscode.workspace.workspaceFolders?.[0].uri.fsPath;
    }

    public async generateDocumentation() {
        if (!this.workspaceRoot) {
            vscode.window.showErrorMessage('FlagWatch: No workspace folder found.');
            return;
        }

        const files = await this.findAllFiles();
        const doc: FeatureFlagDoc = { flags: [] };

        for (const file of files) {
            await this.parseFile(file, doc);
        }

        this.writeDocumentation(doc);
    }

    private async findAllFiles(): Promise<string[]> {
        // Find all TS/JS files in the workspace, ignoring node_modules
        const files = await vscode.workspace.findFiles('**/*.{ts,js}', '**/node_modules/**');
        return files.map(uri => uri.fsPath);
    }

    private async parseFile(filePath: string, doc: FeatureFlagDoc) {
        try {
            const content = fs.readFileSync(filePath, 'utf-8');
            const ast = parser.parse(content, {
                sourceType: 'module',
                plugins: ['typescript', 'jsx', 'classProperties']
            });

            traverse(ast, {
                // Detect variable declarations that look like feature flags
                VariableDeclarator(path) {
                    if (path.node.id.type === 'Identifier') {
                        const varName = path.node.id.name;
                        // SIMPLE HEURISTIC: Variables starting with "FEATURE_" or "FLAG_"
                        if (varName.startsWith('FEATURE_') || varName.startsWith('FLAG_')) {

                            let existingFlag = doc.flags.find(f => f.name === varName);
                            if (!existingFlag) {
                                let initValue = 'undefined';
                                if (path.node.init) {
                                    if (path.node.init.type === 'StringLiteral' || path.node.init.type === 'BooleanLiteral' || path.node.init.type === 'NumericLiteral') {
                                        initValue = String(path.node.init.value);
                                    }
                                }

                                existingFlag = {
                                    name: varName,
                                    defaultValue: initValue,
                                    files: []
                                };
                                doc.flags.push(existingFlag);
                            }
                            // Mark definition file? Maybe just usages for now.
                        }
                    }
                },
                // Detect usages
                Identifier(path) {
                    const varName = path.node.name;
                    // Check if this identifier matches a known flag (or potential flag pattern)
                    // If we already found the definition, good. If not, we might finding usages before def if we parse in arbitrary order.
                    // For simplicity, we'll assume we want to track usages of *any* identifier starting with FEATURE_
                    if (varName.startsWith('FEATURE_') || varName.startsWith('FLAG_')) {
                        let existingFlag = doc.flags.find(f => f.name === varName);
                        if (!existingFlag) {
                            // It's a usage of a flag we haven't seen the definition for yet, or maybe it's defined elsewhere.
                            // Add it to doc.
                            existingFlag = {
                                name: varName,
                                defaultValue: 'unknown',
                                files: []
                            };
                            doc.flags.push(existingFlag);
                        }

                        // Add current file to usage list if not already there
                        // Use relative path for cleaner docs
                        const relativePath = vscode.workspace.asRelativePath(filePath);
                        if (!existingFlag.files.includes(relativePath)) {
                            existingFlag.files.push(relativePath);
                        }
                    }
                }
            });

        } catch (error) {
            console.error(`Error parsing file ${filePath}:`, error);
        }
    }

    private writeDocumentation(doc: FeatureFlagDoc) {
        if (!this.workspaceRoot) {
            return;
        }

        const docPath = path.join(this.workspaceRoot, 'feature_flags_doc.json');
        try {
            fs.writeFileSync(docPath, JSON.stringify(doc, null, 2));
            // Optional: Show status message
            // vscode.window.setStatusBarMessage('FlagWatch: Documentation updated', 2000);
        } catch (error: any) {
            vscode.window.showErrorMessage(`FlagWatch: Failed to write documentation. ${error.message}`);
        }
    }
}
