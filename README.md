<div align="center">

# GamingBytez Enhancements

[![gh-license-badge][gh-license-badge]][gh-license]
[![discord-badge][discord-badge]][discord]

[![gh-contributors-badge][gh-contributors-badge]][gh-contributors]
[![gh-stars-badge][gh-stars-badge]][gh-stars]

</div>

## Description

This project is a simple plugin for out private GamingBytez Minecraft server.
It provides some new features and enhancements to the server.

> At this time the plugin just aims to cover the needs of our server 

## Getting started

### Requirements

1. Git
2. Maven
3. Spigot 1.19

### Setup

Building an artefact:
```bash 
mvn clean package
```
## Contributing

If you want to take part in contribution, like fixing issues and contributing directly to the code base, please visit
the [How to Contribute][gh-contribute] document.

### Commit messages

Construct of a commit message:

```bash
prefix(scope): commit subject with max 50 chars
```

Example commit message:

```bash
feat(comp): add ping slash command
```

#### Scopes

Project specific scopes and what to use them for.

```bash
'deps', // Changes done on anything dependency related
'devops', // Changes done on technical processes
```

#### Prefixes:

Also see [CONTRIBUTING.md#commits](https://github.com/lazybytez/.github/blob/main/docs/CONTRIBUTING.md#commits)

```bash
'feat', // Some new feature that has been added
'fix', // Some fixes to an existing feature
'build', // Some change on how the project is built
'chore', // Some change that just has to be done (like updating dependencies)
'ci', // Some changes to the continues integration workflows
'docs', // Some changes to documentation located in the repo (either markdown files or code DocBlocks)
'perf', // Some performance improvements
'refactor', // Some code changes, that neither adds functionality or fixes a bug
'revert', // Some changes that revert already done changes
'style', // Some fixes regarding code style
'test', // Some automated tests that have been added
```

#### Branches:

| Branch     | Usage                                  |
|------------|----------------------------------------|
| main       | The default branch                     |
| feature/*  | For developing features                |
| fix/*      | For fixing bugs                        |

### Recommended IDEs

- [IntelliJ](https://www.jetbrains.com/idea/) (free & paid)

### Using AI Assistance (Claude Code)

This repository supports the use of [Claude Code](https://claude.ai/code) for development assistance. A `CLAUDE.md` file is provided to help Claude understand the codebase structure and conventions.

**Important guidelines when using AI assistance:**

- **Code quality and style must remain consistent** with the existing codebase
- **Use with caution** - AI-generated code is a tool, not a replacement for understanding
- **All code MUST be thoroughly reviewed** before merging into main
- **Pull requests are mandatory** for any AI-assisted changes
- **Never merge code you don't completely understand** - if the AI generates something unclear, refactor it or ask for clarification
- AI suggestions should follow existing architectural patterns unless you explicitly want to introduce improvements
- Review AI-generated code for security vulnerabilities, edge cases, and maintainability

## Useful links

[License][gh-license] -
[Contributing][gh-contribute] -
[Code of conduct][gh-codeofconduct] -
[Issues][gh-issues] -
[Pull requests][gh-pulls]

<hr>  

###### Copyright (c) [Lazy Bytez][gh-team]. All rights reserved | Licensed under the AGPL-3.0 license.

<!-- Variables -->

[gh-license-badge]: https://img.shields.io/github/license/lazybytez/gamingbytez-enhancements?logo=mit&style=for-the-badge&colorA=302D41&colorB=eba0ac

[gh-license]: https://github.com/lazybytez/gamingbytez-enhancements/blob/main/LICENSE

[discord-badge]: https://img.shields.io/discord/735171597362659328?label=Discord&logo=discord&style=for-the-badge&colorA=302D41&colorB=b4befe

[discord]: https://discord.gg/bcV6TN2k9V

[gh-contributors-badge]: https://img.shields.io/github/contributors/lazybytez/gamingbytez-enhancements?style=for-the-badge&colorA=302D41&colorB=cba6f7

[gh-contributors]: https://github.com/lazybytez/gamingbytez-enhancements/graphs/contributors

[gh-stars-badge]: https://img.shields.io/github/stars/lazybytez/gamingbytez-enhancements?colorA=302D41&colorB=f9e2af&style=for-the-badge

[gh-stars]: https://github.com/lazybytez/gamingbytez-enhancements/stargazers

[gh-contribute]: https://github.com/lazybytez/.github/blob/main/docs/CONTRIBUTING.md

[gh-codeofconduct]: https://github.com/lazybytez/.github/blob/main/docs/CODE_OF_CONDUCT.md

[gh-issues]: https://github.com/lazybytez/gamingbytez-enhancements/issues

[gh-pulls]: https://github.com/lazybytez/gamingbytez-enhancements/pulls

[gh-team]: https://github.com/lazybytez
