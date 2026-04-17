package com.termintracker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.model.Document
import com.termintracker.model.RequiredDocument

@Composable
fun RequiredDocumentsList(
    documents: List<RequiredDocument>,
    onCheckedChange: (RequiredDocument, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Required Documents",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (documents.isEmpty()) {
                Text(
                    text = "No required documents for this appointment type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                documents.forEach { doc ->
                    RequiredDocumentItem(
                        document = doc,
                        onCheckedChange = { onCheckedChange(doc, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequiredDocumentItem(
    document: RequiredDocument,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = document.isChecked,
            onCheckedChange = onCheckedChange
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = document.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (document.description.isNotBlank()) {
                Text(
                    text = document.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (document.isRequired) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Required",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun UploadedDocumentsList(
    documents: List<Document>,
    onAddDocument: () -> Unit,
    onDeleteDocument: (Document) -> Unit,
    onDocumentClick: (Document) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uploaded Documents",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Button(onClick = onAddDocument) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload")
                }
            }
            
            if (documents.isEmpty()) {
                Text(
                    text = "No documents uploaded yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                documents.forEach { doc ->
                    UploadedDocumentItem(
                        document = doc,
                        onDelete = { onDeleteDocument(doc) },
                        onClick = { onDocumentClick(doc) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadedDocumentItem(
    document: Document,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(document.name) },
        supportingContent = {
            if (document.isUploaded && document.uploadDate != null) {
                Text(
                    text = "Uploaded: ${document.uploadDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = if (document.isUploaded) Icons.Default.CheckCircle else Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = if (document.isUploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun FilePickerDialog(
    onDismiss: () -> Unit,
    onFileSelected: (String, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onFileSelected(fileName, filePath) },
                enabled = fileName.isNotBlank()
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Upload Document") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Document Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    label = { Text("File Path (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Note: Actual file selection will open system file picker",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
