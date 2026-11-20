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
