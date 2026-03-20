# Contributing

Thank you for your interest in contributing to GamingBytez Enhancements. To keep everything organised, please read these guidelines before opening an issue or pull request.

## Code of Conduct

Please read our [Code of Conduct][gh-codeofconduct] before contributing. By participating you agree to abide by it.

## Questions, Bugs and Feature Requests

Open a GitHub issue using the appropriate template. Before doing so, search existing issues to avoid duplicates. Always provide a clear and concise description.

### Security Issues

Do **not** open a public GitHub issue for security vulnerabilities — it puts everyone running the plugin at risk. Send a mail to [security@lazybytez.de][email] instead.

## Contributing Code

### Developer Certificate of Origin (DCO)

All contributions must be signed off against the [Developer Certificate of Origin (DCO)](DCO). By signing off you certify that you wrote the code and have the right to submit it under this project's AGPL-3.0 license.

Add a sign-off to every commit using `git commit -s`:

```
Signed-off-by: Your Name <your@email.com>
```

Pull requests with any commit missing a sign-off will be blocked by CI.

### License Headers

Every Java source file must carry the project's AGPL license header. Run the following before submitting a PR:

```bash
mvn license:format
```

CI will reject PRs with missing or incorrect headers.

### Process

1. Open an issue for the change you want to make (or pick an existing one).
2. Comment on the issue to let maintainers know you are working on it.
3. Create a branch from `main` using the naming convention below.
4. Make your changes, sign off every commit, and run `mvn license:format`.
5. Open a pull request against `main`.
6. A maintainer will review your PR. Address any requested changes and re-push.

**Requirements for an approved PR:**

1. All CI checks pass (tests, license headers, DCO sign-off, commit messages).
2. At least one approving review from a maintainer.
3. All review conversations resolved.
4. Linked to an open issue.

### Branching

| Branch | Purpose |
|---|---|
| `main` | Stable — all PRs target this branch |
| `feature/*` | New features |
| `fix/*` | Bug fixes |

### Commit Messages

Follow the conventional commit format:

```
type(scope): short description (max 50 chars)
```

**Types:** `feat`, `fix`, `build`, `chore`, `ci`, `docs`, `perf`, `refactor`, `revert`, `style`, `test`

**Scopes:** use a feature name (e.g. `chatbot`, `mythicaltar`) or `deps` / `devops` for cross-cutting changes.

**Examples:**
```
feat(chatbot): add context tracking for responses
fix(minecartportal): handle null world on reload
docs: update altar structure diagram
```

Commit messages are enforced by CI via commitlint.

## You're all set

We appreciate every contribution — people like you make open source worth it.

If you have suggestions for these guidelines, open an issue or PR.

<!-- Variables -->

[gh-codeofconduct]: https://github.com/lazybytez/.github/blob/main/docs/CODE_OF_CONDUCT.md
[email]: mailto:security@lazybytez.de
