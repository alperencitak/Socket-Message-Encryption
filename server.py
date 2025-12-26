from fastapi import FastAPI, WebSocket
from fastapi.middleware.cors import CORSMiddleware
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.primitives import serialization
import base64

app = FastAPI()

rsa_key = RSA.generate(2048)
rsa_private = rsa_key
rsa_public_pem = rsa_key.publickey().export_key().decode()

ecc_private_key = ec.generate_private_key(ec.SECP256R1())
ecc_public_pem = ecc_private_key.public_key().public_bytes(
    encoding=serialization.Encoding.PEM,
    format=serialization.PublicFormat.SubjectPublicKeyInfo
).decode()

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
        "rsa_public_key": rsa_public_pem,
        "ecc_public_key": ecc_public_pem
    })

    clients.append(websocket)
    session_aes_key = None

    try:
        while True:
            message = await websocket.receive_json()

            if message.get("type") == "rsa_key_exchange":
                encrypted_aes_key = base64.b64decode(message["encrypted_key"])
                cipher_rsa = PKCS1_v1_5.new(rsa_private)
                session_aes_key = cipher_rsa.decrypt(encrypted_aes_key, b'error')
                print(f"AES Anahtarı RSA ile çözüldü: {session_aes_key.hex()}")
                continue

            if message.get("type") == "ecc_key_exchange":
                client_ecc_pub_bytes = base64.b64decode(message["public_key"])
                client_public_key = serialization.load_der_public_key(client_ecc_pub_bytes)
                shared_secret = ecc_private_key.exchange(ec.ECDH(), client_public_key)
                session_des_key = shared_secret[:8]
                print(f"DES Anahtarı ECC ile türetildi: {session_des_key.hex()}")
                continue

            for client in clients:
                if client != websocket:
                    await client.send_json(message)

    except Exception as e:
        print("Client disconnected:", e)
    finally:
        clients.remove(websocket)
