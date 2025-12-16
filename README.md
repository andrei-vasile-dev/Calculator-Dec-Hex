# 📱 Calculator Zecimal / Hexazecimal

Aplicație Android dezvoltată în **Kotlin** pentru disciplina **Dezvoltarea Aplicațiilor Mobile** - Facultatea de Litere și Științe, Specializarea Informatică, UPG Ploiești.

Aplicația folosește Fragments, SQLite, comunicație HTTP, email și sistem de log-uri.

---

## 🧮 Funcționalități principale

- Calculator **zecimal / hexazecimal**
- Tastatură virtuală cu cifre '0-F'
- Operații implementate:
    - ➕ Adunare
    - ➖ Scădere
    - ✖️ Înmulțire
- Conversie automată între baza 10 și baza 16
- Istoric calcule selectabil
- Trimitere istoric prin email
- Comunicare cu server HTTP/HTTPS
- Log conexiuni Internet
- Persistență date cu SQLite


---

## 🧩 Arhitectură aplicație
Aplicația este structurată folosind **Fragments**:

| Fragment | Descriere |
|--------|------------|
| Calcul | Tastatură, operații, conversie de bază |
| Istoric | Istoric calcule (ListView + SQLite) |
| Email | Trimiterea istoricului prin email |
| Log | Afișare log conexiuni Internet |

Navigarea între ecrane se realizează folosind ViewPager2.

---

## 🗄️ Baza de date (SQLite)

Aplicația utilizează o bază de date locală pentru stocarea informațiilor.

### Tabele implementate

**calcul**
- operatie
- operand1
- operand2
- bazanumeratie
- rezultat
- dataora

**istoric**
- linie
- culoare

**emailuri**
- adresa_email

La pornirea aplicației, istoricul este încărcat automat din baza de date. De asemenea, valorile din istoric sunt reprezentate cu două culori: cele care sunt în baza 10 cu verde, iar cele care sunt în baza 16 cu roz.

---

## 🌐 Comunicare cu serverul

La apăsarea butonului '=', aplicația trimite un request HTTPS de forma:
utilizator_bazaNumeratie_operand1_operator_operand2

Rezultatul primit de la server este afișat lângă rezultatul calculat local.

---

## 🧾 Sisitem de log-uri

Aplicația înregistrează următoarele evenimente:

- conectarea la server (succes/eroare)
- trimitere mesaj
- primire răspuns
- erori de rețea
- deconectare

Log-ul este:
- salvat într-un fișier text din **Internal Storage**
- citit la pornirea aplicației
- afișat în fragmentul "Log"

---

## ✉️ Trimitere email

- Istoricul complet poate fi trimis prin email
- Se utilizează **Explicit Intent**
- Adresele de email sunt salvate în SQLite
- Câmpul email oferă sugestii automate bazate pe istoricul trimiterilor

---

## 🎨 Interfață utilizator

- Dezactivare butoane 'A-F' în baza 10
- Conversie automată a valorilor la schimbarea bazei
- Culori diferite pentru baze în istoric
    - baza 10 - verde
    - baza 16 - roz
- Scroll automat la ultimele valori din istoric

---

## 📸 Capturi de ecran


<table>
<tr>
  <td><img src="screenshots/calculator1.jpeg" width="300"></td>
  <td><img src="screenshots/calculator2.jpeg" width="300"></td>
  <td><img src="screenshots/istoric.jpeg" width="300"></td>
</tr>
<tr>
  <td><img src="screenshots/email1.jpeg" width="300"></td>
  <td><img src="screenshots/email2.jpeg" width="300"></td>
  <td><img src="screenshots/log.jpeg" width="300"></td>
</tr>
</table>

## 📄 Documentațe

Documentația completă a implementării se găsește în folder-ul docs.


---

## 🛠️ Tehnologii utilizate

- Kotlin
- Android Fragment
- SQLite
- HTTPS (Volley)
- Explicit Intent (Email)
- Internal Storage

---
