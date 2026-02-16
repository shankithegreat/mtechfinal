/**
 * Mind Map Visualization using Mermaid.js
 */

/**
 * Render mind map from search results
 */
function renderMindMap(results) {
    if (!results || results.length === 0) {
        document.getElementById('mermaid-diagram').textContent = 'No data to visualize';
        return;
    }

    // Build graph structure
    const graph = buildGraphStructure(results);

    // Generate Mermaid syntax
    const mermaidSyntax = generateMermaidSyntax(graph);

    // Render
    const container = document.getElementById('mermaid-diagram');
    container.textContent = mermaidSyntax;
    container.removeAttribute('data-processed');

    // Re-initialize Mermaid
    mermaid.init(undefined, container);
}

/**
 * Build hierarchical graph structure from results
 */
function buildGraphStructure(results) {
    const graph = {
        root: {
            id: 'root',
            label: determineRootLabel(results),
            children: {}
        }
    };

    // Group by service
    results.forEach((result, index) => {
        const serviceName = result.service_name || 'Unknown';

        if (!graph.root.children[serviceName]) {
            graph.root.children[serviceName] = {
                id: sanitizeId(serviceName),
                label: serviceName,
                files: []
            };
        }

        // Add file reference
        const fileName = extractFileName(result.file_path || 'unknown');
        const lineInfo = result.line_number ? `:${result.line_number}` : '';

        graph.root.children[serviceName].files.push({
            id: sanitizeId(`${serviceName}_${fileName}_${index}`),
            label: `${fileName}${lineInfo}`,
            code: truncateText(result.code || result.flag_name || '', 30)
        });
    });

    return graph;
}

/**
 * Determine root node label based on search mode and results
 */
function determineRootLabel(results) {
    if (currentMode === 'flag') {
        const flagSearch = document.getElementById('flag-search').value.trim();
        return flagSearch || 'Feature Flag';
    } else if (currentMode === 'code') {
        return 'Code Pattern';
    } else {
        const naturalQuery = document.getElementById('natural-search').value.trim();
        return truncateText(naturalQuery, 30) || 'Search Query';
    }
}

/**
 * Generate Mermaid flowchart syntax
 */
function generateMermaidSyntax(graph) {
    let mermaid = 'graph TD\n';

    // Root node
    const rootId = graph.root.id;
    const rootLabel = escapeLabel(graph.root.label);
    mermaid += `    ${rootId}["🎯 ${rootLabel}"]\n`;
    mermaid += `    style ${rootId} fill:#6366f1,stroke:#818cf8,stroke-width:3px,color:#fff\n`;

    // Service nodes
    const services = Object.values(graph.root.children);
    services.forEach((service, sIndex) => {
        const serviceId = service.id;
        const serviceLabel = escapeLabel(service.label);

        // Add service node
        mermaid += `    ${serviceId}["📦 ${serviceLabel}"]\n`;
        mermaid += `    ${rootId} --> ${serviceId}\n`;
        mermaid += `    style ${serviceId} fill:#8b5cf6,stroke:#a78bfa,stroke-width:2px,color:#fff\n`;

        // File nodes
        service.files.forEach((file, fIndex) => {
            if (fIndex < 5) {  // Limit to 5 files per service to avoid clutter
                const fileId = file.id;
                const fileLabel = escapeLabel(file.label);

                mermaid += `    ${fileId}["📄 ${fileLabel}"]\n`;
                mermaid += `    ${serviceId} --> ${fileId}\n`;
                mermaid += `    style ${fileId} fill:#ec4899,stroke:#f472b6,stroke-width:1px,color:#fff\n`;
            }
        });

        // Add "more" node if there are more files
        if (service.files.length > 5) {
            const moreId = `${serviceId}_more`;
            const remaining = service.files.length - 5;
            mermaid += `    ${moreId}["... ${remaining} more files"]\n`;
            mermaid += `    ${serviceId} --> ${moreId}\n`;
            mermaid += `    style ${moreId} fill:#64748b,stroke:#94a3b8,stroke-width:1px,color:#fff\n`;
        }
    });

    return mermaid;
}

/**
 * Alternative: D3.js force-directed graph (more advanced)
 * Can be implemented later for more interactive visualization
 */
function renderD3Graph(results) {
    // TODO: Implement D3.js force-directed graph
    // This would provide more interactive features:
    // - Draggable nodes
    // - Zoom and pan
    // - Tooltips with code snippets
    // - Click to expand/collapse
    console.log('D3 graph not yet implemented');
}

/**
 * Helper Functions
 */
function sanitizeId(str) {
    return str
        .replace(/[^a-zA-Z0-9_]/g, '_')
        .replace(/^[0-9]/, 'n$&');  // Mermaid IDs can't start with number
}

function escapeLabel(str) {
    return str
        .replace(/"/g, '\\"')
        .replace(/\n/g, ' ')
        .substring(0, 40);  // Limit label length
}

function extractFileName(filePath) {
    const parts = filePath.split(/[\\\/]/);
    return parts[parts.length - 1] || 'unknown';
}

/**
 * Export graph as image (future feature)
 */
function exportGraphAsImage() {
    // Use mermaid.render() to generate SVG
    // Then convert to PNG using canvas
    console.log('Export feature not yet implemented');
}
