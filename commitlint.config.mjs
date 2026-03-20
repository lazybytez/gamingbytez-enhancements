export default {
    extends: ['@commitlint/config-conventional'],
    rules: {
        'type-enum': [
            2,
            'always',
            [
                'feat',
                'fix',
                'build',
                'chore',
                'ci',
                'docs',
                'perf',
                'refactor',
                'revert',
                'style',
                'test',
                'sec',
            ],
        ],
        'subject-max-length': [2, 'always', 50],
    },
};
