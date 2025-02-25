# 🌤️ WeatherApp

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-4285F4.svg?style=flat-square&logo=jetpackcompose&logoColor=white)
![Dagger Hilt](https://img.shields.io/badge/Dagger%20Hilt-2.48.1-E91E63.svg?style=flat-square&logo=dagger&logoColor=white)
![MVI](https://img.shields.io/badge/MVI-FF6200.svg?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-4CAF50.svg?style=flat-square)

**WeatherApp** — это современное Android-приложение для просмотра погоды, построенное с использованием чистой архитектуры и MVI-подхода. Оно позволяет пользователям искать города, просматривать текущую погоду и прогноз, а также управлять списком избранных городов.

---

## ✨ Основные функции

- 🔍 **Поиск городов**: Быстрый поиск городов с помощью API WeatherAPI.
- 🌡️ **Погода**: Отображение текущей погоды и прогноза на 4 дня.
- ⭐ **Избранное**: Добавление и удаление городов в список избранных с локальным сохранением.
- 🔄 **Адаптивный UI**: Интуитивный интерфейс с поддержкой анимаций и градиентов.
- 🚨 **Обработка ошибок**: Отображение состояний загрузки и ошибок.

---

## 🛠 Технологии

| Категория            | Технология         | Описание                              |
|----------------------|--------------------|---------------------------------------|
| **Язык**            | Kotlin            | Основной язык разработки             |
| **UI**              | Jetpack Compose   | Декларативный UI-фреймворк           |
| **Архитектура**     | Clean Architecture| Разделение на слои: Data, Domain, Presentation |
| **Паттерн**         | MVI (Decompose)   | Управление состоянием через MVIKotlin|
| **DI**              | Dagger Hilt       | Внедрение зависимостей               |
| **Сеть**            | Retrofit          | Запросы к WeatherAPI                 |
| **База данных**     | Room              | Локальное хранение избранных городов |
| **Изображения**     | Glide Compose     | Загрузка погодных иконок             |
| **Навигация**       | Decompose Router  | Навигация между экранами             |

---

## Иструкция по сборке проекта: 

В файл gradle.properties необхоимо добавить ваш apikey в следующем формате

apikey=YOUR_API_KEY
