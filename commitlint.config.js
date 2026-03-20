module.exports = {
    rules: {
        'type-enum': [
            2,
            'always',
            ['feat', 'fix', 'build', 'chore', 'ci', 'docs', 'perf', 'refactor', 'revert', 'style', 'test'],
        ],
        'type-case': [2, 'always', 'lower-case'],
        'type-empty': [2, 'never'],
        'scope-case': [2, 'always', 'lower-case'],
        'subject-empty': [2, 'never'],
        'subject-max-length': [2, 'always', 50],
        'subject-case': [2, 'always', 'lower-case'],
        'header-max-length': [2, 'always', 72],
    },
};
