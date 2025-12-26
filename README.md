# Socket Message Encryption

Gerçek zamanlı mesajlaşma uygulaması üzerinde **klasik ve modern şifreleme algoritmalarının** kullanımını gösteren, **WebSocket tabanlı** bir Android & FastAPI projesidir.

Bu proje, farklı şifreleme algoritmalarının mesaj güvenliğine etkisini **uygulamalı olarak incelemek ve karşılaştırmak** amacıyla geliştirilmiştir.




## Proje Amacı

* WebSocket kullanarak **gerçek zamanlı iletişim** sağlamak
* Mesajları farklı **şifreleme algoritmalarıyla** şifreleyerek göndermek
* Klasik ve modern şifreleme yöntemlerini **uygulamalı olarak göstermek**
* Şifreleme algoritmalarının performans ve kullanım farklarını gözlemlemek




## Kullanılan Teknolojiler

### Backend

* **FastAPI**
* **WebSocket**
* **Python**

### Frontend

* **Android (Kotlin)**
* **Jetpack Compose**
* **MVVM Mimari Yapısı**
* **OkHttp WebSocket**
* **Hilt (Dependency Injection)**




## Desteklenen Şifreleme Algoritmaları

### Klasik Şifreleme Yöntemleri

* Caesar Cipher
* Vigenère Cipher
* Affine Cipher
* Hill Cipher
* Substitution Cipher
* Playfair Cipher
* Rail Fence Cipher
* Route Cipher
* Columnar Transposition Cipher
* Polybius Cipher
* Pigpen Cipher
* One Time Pad Cipher

### Modern Şifreleme Yöntemleri

* AES (Manual)
* AES (Library)
* DES (Manual)
* DES (Library)
* RSA (Manual)
* RSA (Library)




## Uygulama Akışı

1. Kullanıcı kullanıcı adını girer
2. Kullanıcı şifreleme algoritmasını seçer
3. WebSocket bağlantısı kurulur
4. Gönderilen mesaj:
   * Seçilen algoritma ile şifrelenir
   * JSON formatında server'a gönderilir
5. Alıcı tarafta mesaj çözülür ve sohbet ekranında gösterilir




## WebSocket Mesaj Formatı

```json
{
  "sender": "username",
  "message": "encrypted_message"
}
```




## Mimari Yapı (Android)

```
feature/
 └── home/
     ├── ChatViewModel.kt
     ├── LoginView.kt
     ├── ChatView.kt
     └── components/
```

* **ViewModel** → WebSocket ve şifreleme işlemleri
* **UI (Compose)** → Kullanıcı arayüzü
* **Algorithm Interface** → Strategy Pattern uygulanmıştır




## Kullanılan Tasarım Deseni

Bu projede **Strategy Design Pattern** kullanılmıştır.

```kotlin
interface Algorithm {
    fun encrypt(text: String, shift: Int): String
    fun decrypt(text: String, shift: Int): String
}
```

Her şifreleme algoritması bu interface'i implemente eder ve runtime sırasında değiştirilebilir.




## Kurulum

### Backend

```bash
pip install fastapi uvicorn
uvicorn main:app --host 0.0.0.0 --port 12345
```

### Android

* Android Studio ile projeyi açın
* Emülatör veya fiziksel cihazda çalıştırın
* Backend IP adresini girerek bağlantı kurun



## Arayüz 

<img width="365" height="683" alt="Image" src="https://github.com/user-attachments/assets/d8832759-fb75-4963-9aae-7892a7678813" />
<img width="390" height="640" alt="Image" src="https://github.com/user-attachments/assets/1bc5de11-caaf-439b-b339-bb64d4fb293f" />
<img width="403" height="618" alt="Image" src="https://github.com/user-attachments/assets/606ed768-c03f-413a-97d9-71b5f9b9a83e" />

## Örnek AES Şifrelemesi

<img width="498" height="317" alt="Image" src="https://github.com/user-attachments/assets/8eeca110-ae4f-4e9f-8032-0f2ee22ad366" />

<img width="943" height="600" alt="Image" src="https://github.com/user-attachments/assets/70fe928e-6a44-46c3-992b-86e4debb4138" />
