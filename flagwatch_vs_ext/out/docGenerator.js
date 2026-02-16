"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.DocGenerator = void 0;
const vscode = __importStar(require("vscode"));
const fs = __importStar(require("fs"));
const path = __importStar(require("path"));
const parser = __importStar(require("@babel/parser"));
const traverse_1 = __importDefault(require("@babel/traverse"));
class DocGenerator {
    constructor() {
        this.workspaceRoot = vscode.workspace.workspaceFolders?.[0].uri.fsPath;
    }
    async generateDocumentation() {
        if (!this.workspaceRoot) {
            vscode.window.showErrorMessage('FlagWatch: No workspace folder found.');
            return;
        }
        const files = await this.findAllFiles();
        const doc = { flags: [] };
        for (const file of files) {
            await this.parseFile(file, doc);
        }
        this.writeDocumentation(doc);
    }
    async findAllFiles() {
        // Find all TS/JS files in the workspace, ignoring node_modules
        const files = await vscode.workspace.findFiles('**/*.{ts,js}', '**/node_modules/**');
        return files.map(uri => uri.fsPath);
    }
    async parseFile(filePath, doc) {
        try {
            const content = fs.readFileSync(filePath, 'utf-8');
            const ast = parser.parse(content, {
                sourceType: 'module',
                plugins: ['typescript', 'jsx', 'classProperties']
            });
            (0, traverse_1.default)(ast, {
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
        }
        catch (error) {
            console.error(`Error parsing file ${filePath}:`, error);
        }
    }
    writeDocumentation(doc) {
        if (!this.workspaceRoot) {
            return;
        }
        const docPath = path.join(this.workspaceRoot, 'feature_flags_doc.json');
        try {
            fs.writeFileSync(docPath, JSON.stringify(doc, null, 2));
            // Optional: Show status message
            // vscode.window.setStatusBarMessage('FlagWatch: Documentation updated', 2000);
        }
        catch (error) {
            vscode.window.showErrorMessage(`FlagWatch: Failed to write documentation. ${error.message}`);
        }
    }
}
exports.DocGenerator = DocGenerator;
//# sourceMappingURL=docGenerator.js.map