from fastapi import FastAPI, WebSocket
from fastapi.middleware.cors import CORSMiddleware
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5
import base64

app = FastAPI()

key = RSA.generate(2048)
private_key = key
public_key = key.publickey().export_key().decode()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

clients = []


@app.websocket("/ws/chat")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()

    await websocket.send_json({
        "type": "handshake",
        "public_key": public_key
    })

    clients.append(websocket)
    session_aes_key = None

    try:
        while True:
            message = await websocket.receive_json()

            if message.get("type") == "key_exchange":
                encrypted_aes_key = base64.b64decode(message["encrypted_key"])
                cipher_rsa = PKCS1_v1_5.new(private_key)
                sentinel = b'error'
                session_aes_key = cipher_rsa.decrypt(encrypted_aes_key, sentinel)
                print(f"AES Anahtarı başarıyla çözüldü: {session_aes_key}")
                continue

            for client in clients:
                if client != websocket:
                    await client.send_json(message)

    except Exception as e:
        print("Client disconnected:", e)
    finally:
        clients.remove(websocket)
