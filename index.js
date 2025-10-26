// index.js
const express = require("express");
const multer = require("multer");
const axios = require("axios");
const FormData = require("form-data");

const app = express();
const upload = multer({ storage: multer.memoryStorage() });

// Replace this with your Telegram bot token
const TELEGRAM_BOT_TOKEN = "8372924450:AAGgPPZ_mpdORPZG2PVaDOt9ymUeE2RVfoE";
const TELEGRAM_CHAT_ID = "8049244190"; // your personal chat or group id

app.get("/", (req, res) => {
  res.send(`
    <h2>📸 Telegram Auto Photo Uploader</h2>
    <form action="/upload" method="POST" enctype="multipart/form-data">
      <input type="file" name="photo" accept="image/*" required>
      <button type="submit">Upload & Send</button>
    </form>
  `);
});

app.post("/upload", upload.single("photo"), async (req, res) => {
  try {
    const formData = new FormData();
    formData.append("chat_id", TELEGRAM_CHAT_ID);
    formData.append("photo", req.file.buffer, req.file.originalname);

    const url = `https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendPhoto`;

    const response = await axios.post(url, formData, {
      headers: formData.getHeaders(),
    });

    res.send("✅ Photo uploaded successfully to Telegram!");
  } catch (err) {
    console.error(err);
    res.status(500).send("❌ Failed to upload photo to Telegram.");
  }
});

// Render uses PORT environment variable
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`✅ Server running on port ${PORT}`));
