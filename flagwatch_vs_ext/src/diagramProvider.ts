import * as vscode from 'vscode';

export class DepDiagramProvider implements vscode.WebviewViewProvider {

    public static readonly viewType = 'flagwatch.dependencyView';

    private _view?: vscode.WebviewView;

    constructor(
        private readonly _extensionUri: vscode.Uri,
    ) { }

    public resolveWebviewView(
        webviewView: vscode.WebviewView,
        _context: vscode.WebviewViewResolveContext,
        _token: vscode.CancellationToken,
    ) {
        this._view = webviewView;

        webviewView.webview.options = {
            // Allow scripts in the webview
            enableScripts: true,

            localResourceRoots: [
                this._extensionUri
            ]
        };

        webviewView.webview.html = this._getHtmlForWebview(webviewView.webview);

        webviewView.webview.onDidReceiveMessage(data => {
            switch (data.type) {
                case 'onInfo':
                    {
                        if (!data.value) {
                            return;
                        }
                        vscode.window.showInformationMessage(data.value);
                        break;
                    }
                case 'onError':
                    {
                        if (!data.value) {
                            return;
                        }
                        vscode.window.showErrorMessage(data.value);
                        break;
                    }
            }
        });
    }

    public updateGraph(graphData: any) {
        if (this._view) {
            this._view.webview.postMessage({ type: 'updateGraph', data: graphData });
        }
    }

    private _getHtmlForWebview(_webview: vscode.Webview) {
        // In a real extension, we would load the mermaid library from a local resource
        // For this example, we will use a CDN for simplicity, but in a production extension
        // you should vendor the library or use a webview-ui-toolkit.
        // However, VS Code webviews have strict CSP.
        // We will assume the user has internet access or we would include the library in the extension.
        // A better approach for offline support is to include the mermaid.min.js in the extension's `media` folder.

        // For this implementation, I will generate a simple HTML that uses mermaid via CDN 
        // aiming for the simplest working solution as per the prompt requirements.

        return `<!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Feature Flag Dependencies</title>
                <script type="module">
                    import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                    mermaid.initialize({ startOnLoad: true });
                    
                    window.addEventListener('message', event => {
                        const message = event.data;
                        switch (message.type) {
                            case 'updateGraph':
                                update(message.data);
                                break;
                        }
                    });

                    function update(data) {
                        const element = document.getElementById('mermaid-graph');
                        // Simple transformation from JSON data to Mermaid syntax
                        // This assumes data is in a format like { flags: [{ name: 'A', dependencies: ['B', 'C'] }] }
                        let graphDefinition = 'graph TD;\\n';
                        
                        // Example parsing logic - needs to match the actual JSON structure from docGenerator
                        if (data && data.flags) {
                             data.flags.forEach(flag => {
                                graphDefinition += \`    \${flag.name}[ \${flag.name} ]\\n\`;
                                if (flag.files) {
                                    flag.files.forEach(file => {
                                        // Sanitize file path/name for mermaid id
                                        const cleanFile = file.replace(/[^a-zA-Z0-9]/g, '_');
                                        graphDefinition += \`    \${flag.name} --> \${cleanFile}(\${file})\\n\`;
                                    });
                                }
                             });
                        } else {
                            graphDefinition += '    Start --> End\\n';
                        }

                        element.innerHTML = graphDefinition;
                        element.removeAttribute('data-processed');
                        mermaid.contentLoaded();
                    }
                </script>
            </head>
            <body>
                <div class="mermaid" id="mermaid-graph">
                    graph TD;
                    Init[Waiting for Data] --> Load[Scanning Workspace];
                </div>
            </body>
            </html>`;
    }
}
