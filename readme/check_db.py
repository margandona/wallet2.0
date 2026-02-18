import sqlite3

conn = sqlite3.connect('wallet.db')
cur = conn.cursor()

# Get all tables
cur.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = cur.fetchall()

print("Tables in database:")
for table in tables:
    print(f"  - {table[0]}")

# If users table exists, count rows
if any('usuario' in t[0].lower() for t in tables):
    table_name = [t[0] for t in tables if 'usuario' in t[0].lower()][0]
    cur.execute(f"SELECT COUNT(*) FROM {table_name}")
    count = cur.fetchone()[0]
    print(f"\nRows in {table_name}: {count}")
    
    if count > 0:
        cur.execute(f"SELECT * FROM {table_name} LIMIT 5")
        cols = [description[0] for description in cur.description]
        print(f"Columns: {cols}")
        for row in cur.fetchall():
            print(f"  Row: {row}")

conn.close()
