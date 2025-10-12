from algorithms import Algorithms


class CaesarCipher(Algorithms):
    def __init__(self, shift=3):
        self.shift = shift

    def encrypt(self, text: str) -> str:
        result = ""
        for char in text:
            if char.isalpha():
                start = ord('A') if char.isupper() else ord('a')
                result += chr((ord(char) - start + self.shift) % 26 + start)
            else:
                result += char
        return result

    def decrypt(self, text: str) -> str:
        result = ""
        for char in text:
            if char.isalpha():
                start = ord('A') if char.isupper() else ord('a')
                result += chr((ord(char) - start + (-self.shift)) % 26 + start)
            else:
                result += char
        return result
