import socket
import threading
from algorithms import Algorithms
from caesar_cipher import CaesarCipher


class ChatClient:
    def __init__(self, host="127.0.0.1", port=12345, algorithm: Algorithms = None):
        self.host = host
        self.port = port
        self.algorithm = algorithm
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.client_socket.connect((self.host, self.port))

    def start(self):
        threading.Thread(target=self.receive_messages, daemon=True).start()
        threading.Thread(target=self.send_messages, daemon=True).start()
        while True:
            pass

    def receive_messages(self):
        while True:
            try:
                encrypted_data = self.client_socket.recv(1024).decode()
                if not encrypted_data:
                    break
                print(f"\nEncrypted Message: {encrypted_data}")
                print(f"Decrypted Message: {self.algorithm.decrypt(encrypted_data)}")
            except Exception as e:
                print(e)
                break

    def send_messages(self):
        while True:
            msg = input("")
            encrypted_msg = self.algorithm.encrypt(msg)
            self.client_socket.send(encrypted_msg.encode())


if __name__ == "__main__":
    caesar: Algorithms = CaesarCipher(shift=3)
    client = ChatClient(algorithm=caesar)
    client.start()
