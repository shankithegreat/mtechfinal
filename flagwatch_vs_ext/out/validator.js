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
exports.FlagValidator = void 0;
const vscode = __importStar(require("vscode"));
const parser = __importStar(require("@babel/parser"));
const traverse_1 = __importDefault(require("@babel/traverse"));
class FlagValidator {
    constructor() {
        this.diagnosticCollection = vscode.languages.createDiagnosticCollection('flagwatch');
    }
    validateDocument(document) {
        if (document.languageId !== 'typescript' && document.languageId !== 'javascript') {
            return;
        }
        const diagnostics = [];
        const content = document.getText();
        try {
            const ast = parser.parse(content, {
                sourceType: 'module',
                plugins: ['typescript', 'jsx', 'classProperties']
            });
            (0, traverse_1.default)(ast, {
                // Rule 1: Detect string literals that look like flags
                StringLiteral(path) {
                    const value = path.node.value;
                    if (value.startsWith('FEATURE_') || value.startsWith('FLAG_')) {
                        // Check if it's being assigned to a variable named the same (definition) - skip
                        // Check if it's inside a comparison or function call argument (usage) - flag it
                        // Heuristic: If we see a string literal "FEATURE_X", it's likely a magic string usage.
                        // We want users to use constants: MyFlags.FEATURE_X
                        const diagnostic = new vscode.Diagnostic(new vscode.Range(document.positionAt(path.node.start || 0), document.positionAt(path.node.end || 0)), `FlagWatch: Avoid using string literal "${value}" for feature flags. Define a constant instead.`, vscode.DiagnosticSeverity.Warning);
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
                        let output = path.findParent((p) => p.isTryStatement());
                        // If not found, warn
                        if (!output) {
                            const diagnostic = new vscode.Diagnostic(new vscode.Range(document.positionAt(path.node.start || 0), document.positionAt(path.node.end || 0)), `FlagWatch: Feature flag logic "${name}" is not wrapped in a try/catch block. Risk of unhandled errors.`, vscode.DiagnosticSeverity.Warning);
                            // Only add if we haven't already flagged this location (unlikely for Identifiers)
                            diagnostics.push(diagnostic);
                        }
                    }
                }
            });
        }
        catch (e) {
            // Parser error, ignore
            console.error(e);
        }
        this.diagnosticCollection.set(document.uri, diagnostics);
    }
    dispose() {
        this.diagnosticCollection.dispose();
    }
}
exports.FlagValidator = FlagValidator;
//# sourceMappingURL=validator.js.map