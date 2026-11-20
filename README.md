# OMNIVERSE X · ALPHA 1.0

AI + 3D 행성 월드 + 게임 + 소셜 + 가상 경제 + 창작 도구를 하나로 연결한 웹 플랫폼입니다.
전체 소스는 `omniverse-x-alpha1.zip` 안의 `omniverse-x/` 폴더에 있습니다 (자세한 설명: 압축 안의 README.md).

## Render 배포 설정

| 항목 | 값 |
|---|---|
| Runtime | Python 3 (`PYTHON_VERSION=3.12.7`) |
| Region | Singapore |
| Build Command | `python3 -m zipfile -e omniverse-x-alpha1.zip . && pip install -r omniverse-x/requirements.txt` |
| Start Command | `cd omniverse-x && python migrate.py && gunicorn app:app --workers 1 --threads 4 --timeout 120 --bind 0.0.0.0:$PORT` |
| Health Check | `/healthz` |

## 환경변수

| 변수 | 값 |
|---|---|
| `DATABASE_URL` | Supabase `omniverse` 프로젝트의 **Session pooler** 주소 (`[ ]` 없이 비밀번호 입력, 끝에 `?sslmode=require`) |
| `SECRET_KEY` | 긴 임의 문자열 |
| `GEMINI_API_KEY` | Google AI Studio API 키 |
| `GEMINI_MODEL` | `gemini-2.5-flash` |
| `ADMIN_EMAILS` | 관리자로 쓸 이메일 (먼저 그 이메일로 가입한 뒤 설정 권장) |

데이터베이스는 Supabase(서울)에 이미 만들어져 있고, 시작할 때 `migrate.py`가 빠진 마이그레이션만 적용합니다.

## 📱 안드로이드 앱 (APK)

**항상 최신 버전 다운로드 (고정 링크):**
https://github.com/gimcheonbugogcho27-bot/omniverse/releases/latest/download/omniverse-x.apk

- `android/` 폴더의 앱이 OMNIVERSE X 사이트를 앱 화면으로 띄웁니다. 로그인은 앱 안에 유지됩니다.
- `android/` 폴더를 바꿔 main에 올리거나, GitHub → Actions → **Android APK 빌드** → Run workflow를 누르면 새 APK가 만들어집니다.
- 빌드마다 버전 번호가 올라가서 기존 앱 위에 업데이트로 설치됩니다.
- 서명 키는 `android/keystore/`에 있습니다. 저장소가 공개라 누구나 이 키를 볼 수 있으니, 앱을 널리 배포할 때는 새 키를 만들어 저장소 Settings → Secrets에 `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`로 넣으세요(넣으면 그 키가 우선 사용됩니다).
