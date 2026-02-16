import * as vscode from 'vscode';
import { DocGenerator } from './docGenerator';
import { FlagValidator } from './validator';
import { DepDiagramProvider } from './diagramProvider';

export function activate(context: vscode.ExtensionContext) {
    console.log('FlagWatch is now active!');

    const docGenerator = new DocGenerator();
    const validator = new FlagValidator();
    const diagramProvider = new DepDiagramProvider(context.extensionUri);

    // Register Webview Provider
    context.subscriptions.push(
        vscode.window.registerWebviewViewProvider(DepDiagramProvider.viewType, diagramProvider)
    );

    // Command to manually Update Documentation
    context.subscriptions.push(
        vscode.commands.registerCommand('flagwatch.generateDocs', async () => {
            await docGenerator.generateDocumentation();
            // Also update the diagram if open
            // In a real app we'd broadcast the new data.
        })
    );

    // Event Listener: On Save
    context.subscriptions.push(
        vscode.workspace.onDidSaveTextDocument(async (document) => {
            if (document.languageId === 'typescript' || document.languageId === 'javascript') {
                // Update Docs
                await docGenerator.generateDocumentation();

                // Validate
                validator.validateDocument(document);
            }
        })
    );

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
    context.subscriptions.push(
        vscode.window.onDidChangeActiveTextEditor(editor => {
            if (editor) {
                validator.validateDocument(editor.document);
            }
        })
    );

    context.subscriptions.push(
        vscode.workspace.onDidChangeTextDocument(event => {
            // Live validation (could be debounced)
            validator.validateDocument(event.document);
        })
    );
}

export function deactivate() { }
