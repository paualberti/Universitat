#!/bin/bash

# Find all zip files in the current directory and its subdirectories
find . -type f -name "*.zip" | while read -r zip_file; do
    # Get the directory of the zip file
    dir=$(dirname "$zip_file")

    # Get the base name of the zip file without the extension
    base_name=$(basename "$zip_file" .zip)

    # Create a directory with the same name as the zip file
    target_dir="$dir/$base_name"
    mkdir -p "$target_dir"

    # Unzip the contents into the created directory
    echo "Unzipping $zip_file to $target_dir"
    unzip -q "$zip_file" -d "$target_dir"
done
