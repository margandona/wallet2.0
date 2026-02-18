#!/usr/bin/env python3
import subprocess
import time
import threading
import sys

def run_app():
    """Starts the app and feeds input"""
    import os
    os.chdir(r"C:\Users\marga\Desktop\NeekWorld\boot android\wallet")
    
    # Delete old database
    try:
        os.remove("wallet.db")
        print("✓ Deleted old database")
    except:
        pass
    
    # Start Java app
    proc = subprocess.Popen(
        [r"C:\Program Files\Java\jdk-21\bin\java.exe", 
         "-jar", "target/wallet-app-1.0.0-jar-with-dependencies.jar"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1
    )
    
    # Wait for menu to appear
    time.sleep(8)
    
    print("\n=== SENDING COMMANDS ===")
    
    # Commands: 1=Users, 1=Create, data, answers
    commands = [
        "",  # Continue after welcome
        "1",  # Go to Users
        "1",  # Create new user
        "Juan",  # Name
        "Perez",  # Last name
        "juan@test.com",  # Email
        "1",  # DNI
        "12345678",  # Document
        "s",  # Confirm
        "n",  # Don't create account
        "2",  # List users
        "",  # Continue
        "0",  # Back
        "0",  # Exit
    ]
    
    for cmd in commands:
        time.sleep(0.5)
        print(f"Sending: '{cmd}'")
        try:
            proc.stdin.write(cmd + "\n")
            proc.stdin.flush()
        except:
            break
    
    # Wait for app to exit
    proc.wait(timeout=30)
    print("\n✓ App closed")
    
    # Check database
    import sqlite3
    time.sleep(1)
    
    print("\n=== CHECKING DATABASE ===")
    try:
        conn = sqlite3.connect("wallet.db")
        cur = conn.cursor()
        
        cur.execute("SELECT COUNT(*) FROM usuarios")
        count = cur.fetchone()[0]
        print(f"Total usuarios in DB: {count}")
        
        if count > 0:
            cur.execute("SELECT id, nombre, apellido, email FROM usuarios")
            for row in cur.fetchall():
                print(f"  - {row[1]} {row[2]} ({row[3]})")
        
        conn.close()
        
        if count > 0:
            print("\n✓ Users were saved to database!")
            return True
        else:
            print("\n✗ No users found in database")
            return False
            
    except Exception as e:
        print(f"✗ Error checking database: {e}")
        return False

if __name__ == "__main__":
    success = run_app()
    sys.exit(0 if success else 1)
