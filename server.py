import socket
import threading
from algorithms import Algorithms
from caesar_cipher import CaesarCipher


class ChatServer:
    def __init__(self, host="127.0.0.1", port=12345, algorithm: Algorithms = None):
        self.host = host
        self.port = port
        self.algorithm = algorithm
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.conn = None
        self.addr = None

    def start(self):
        self.server_socket.bind((self.host, self.port))
        self.server_socket.listen()
        print(f"Server başlatıldı. Client bekleniyor")
        self.conn, self.addr = self.server_socket.accept()
        print(f"Bağlanan: {self.addr}")

        threading.Thread(target=self.receive_messages, daemon=True).start()
        threading.Thread(target=self.send_messages, daemon=True).start()
        while True:
            pass

    def receive_messages(self):
        while True:
            try:
                encrypted_data = self.conn.recv(1024).decode()
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
            self.conn.send(encrypted_msg.encode())


if __name__ == "__main__":
    caesar: Algorithms = CaesarCipher(shift=3)
    server = ChatServer(algorithm=caesar)
    server.start()
