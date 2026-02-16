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
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = activate;
exports.deactivate = deactivate;
const vscode = __importStar(require("vscode"));
const docGenerator_1 = require("./docGenerator");
const validator_1 = require("./validator");
const diagramProvider_1 = require("./diagramProvider");
function activate(context) {
    console.log('FlagWatch is now active!');
    const docGenerator = new docGenerator_1.DocGenerator();
    const validator = new validator_1.FlagValidator();
    const diagramProvider = new diagramProvider_1.DepDiagramProvider(context.extensionUri);
    // Register Webview Provider
    context.subscriptions.push(vscode.window.registerWebviewViewProvider(diagramProvider_1.DepDiagramProvider.viewType, diagramProvider));
    // Command to manually Update Documentation
    context.subscriptions.push(vscode.commands.registerCommand('flagwatch.generateDocs', async () => {
        await docGenerator.generateDocumentation();
        // Also update the diagram if open
        // In a real app we'd broadcast the new data.
    }));
    // Event Listener: On Save
    context.subscriptions.push(vscode.workspace.onDidSaveTextDocument(async (document) => {
        if (document.languageId === 'typescript' || document.languageId === 'javascript') {
            // Update Docs
            await docGenerator.generateDocumentation();
            // Validate
            validator.validateDocument(document);
        }
    }));
    // Event Listener: Git Integration
    // We can verify documentation on commit by checking if the Git extension is active
    // and potentially adding a pre-commit check or just ensuring docs are up to date.
    // For this implementation, we will watch for .git/HEAD changes as a proxy for commit activity
    // to trigger a re-scan/validate.
    const gitWatcher = vscode.workspace.createFileSystemWatcher('**/.git/HEAD');
    context.subscriptions.push(gitWatcher);
    gitWatcher.onDidChange(() => {
        console.log('FlagWatch: Git commit detected (HEAD changed). Verifying documentation...');
        docGenerator.generateDocumentation();
    });
    // Initial Validation of active document
    if (vscode.window.activeTextEditor) {
        validator.validateDocument(vscode.window.activeTextEditor.document);
    }
    // Listen for active editor changes to validate
    context.subscriptions.push(vscode.window.onDidChangeActiveTextEditor(editor => {
        if (editor) {
            validator.validateDocument(editor.document);
        }
    }));
    context.subscriptions.push(vscode.workspace.onDidChangeTextDocument(event => {
        // Live validation (could be debounced)
        validator.validateDocument(event.document);
    }));
}
function deactivate() { }
//# sourceMappingURL=extension.js.map