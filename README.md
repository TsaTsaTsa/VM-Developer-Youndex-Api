# Автоматизация Развертывания в Yandex Cloud

Этот проект автоматизирует развертывание и настройку сетевой инфраструктуры в Yandex Cloud с использованием API Yandex Cloud. Цель — упростить и ускорить создание виртуальных машин (ВМ), настройку сети и выполнение команд на удаленных серверах.

## Основные Функции

- **Создание виртуальных машин**: определяете параметры (ядра, память, диск, ОС) в `.ini`-файле, и утилита автоматически создает необходимое количество ВМ
- **Настройка сети**: включает назначение публичного IP и настройку SSH-доступа
- **Удалённое выполнение команд**: автоматически выполняет bash-скрипты на созданных ВМ через SSH
- **Мониторинг готовности**: следит за статусом ВМ и проверяет их готовность к работе

## Используемые Технологии
- **Язык**: Java 21
- **API Yandex Cloud**: используется `java-sdk-services` для создания и управления ВМ
- **SSH**: библиотека `com.hierynomus` для подключения к ВМ и выполнения команд
- **INI-файлы**: библиотека `org.ini4j` для чтения конфигурации ВМ

## Запуск Программы

Для запуска приложения нужно указать путь к `.ini`-файлу как параметр командной строки. Также понадобится переменная окружения `OAUTH_TOKEN` для аутентификации в Yandex Cloud.

Примеры `.ini`-файлов:

### Пример 1: создание ВМ

```ini
[VM]
count=1  # количество виртуальных машин
folder_id=b1gbssc7fd3bno967hd8
prefix=vm-prefix
zone_id=ru-central1-b
platform_id=standard-v1
core=2
memory=4  # в GB
subnet_id=e2ls77p2fg1kukslaubs
disk_size=10  # в GB
image_standard=standard-images
image_family=ubuntu-1804
user_name=some-user-name
path_ssh=path_to_your-ssh-key
commands_file_path=/user_commands.txt  # Путь к bash-скрипту
```

### Пример 2: подключение к существующей ВМ и выполнение команд

```ini
[DEPLOY]
user_name=lena
host=100.000.00.100
commands_file_path=src/main/resources/user_commands.txt
```

## Требования
- Java 21
- OAuth-токен для Yandex Cloud
- SSH-ключ для доступа к ВМ

## Установка и Использование
1. Склонируйте репозиторий с GitHub.
2. Подготовьте `.ini`-файл с параметрами ВМ.
3. Запустите приложение с указанием пути к `.ini`-файлу.

```sh
java -jar deployer.jar path/to/config.ini
```
