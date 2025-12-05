import os

path = r"psmo-community/backend/src/main/resources/db/migration"
if not os.path.exists(path):
    print(f"Path not found: {path}")
else:
    for filename in os.listdir(path):
        print(f"File: {repr(filename)}")
        print(f"  Hex: {filename.encode('utf-8').hex()}")
