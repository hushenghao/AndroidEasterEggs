# Security Policy

## Supported Versions

Security fixes are provided only for the latest stable release line published from the `main` branch.

Older releases may remain available for historical reference, but they should not be considered supported for security updates.

## Reporting a Vulnerability

Please do not report security vulnerabilities through public GitHub issues, discussions, or pull requests.

If GitHub private vulnerability reporting is available for this repository, use that channel first. As a backup contact method, you can email: dede.hu@qq.com

When reporting a vulnerability, please include:

- A clear description of the issue
- The affected app version and Android version
- Steps to reproduce
- Proof-of-concept code, logs, screenshots, or screen recordings when relevant
- A brief explanation of the expected security impact

An initial response is typically expected within 7 days. After triage, the maintainer may confirm whether the report is accepted, request more detail, or explain why the report is out of scope.

## Disclosure Policy

Please allow reasonable time for investigation, remediation, and release preparation before publicly disclosing a suspected vulnerability.

Once a fix is available, the resolution may be documented in release notes, commit history, or a GitHub security advisory when appropriate.

## Scope

Examples of issues that may be considered security vulnerabilities include:

- Unauthorized access to exported components
- Deep link or intent handling vulnerabilities
- Sensitive data exposure
- Insecure file sharing or URI permission handling
- WebView-related code execution or data leakage
- Vulnerabilities that allow privilege escalation, spoofing, or bypass of expected security boundaries

The following are usually not considered security vulnerabilities unless they directly cause a security impact:

- General crashes or ANRs
- UI or layout bugs
- Feature requests
- Device-specific compatibility issues
- Third-party service outages or policy changes outside this repository
