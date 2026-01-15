use v5.10;
say eval join '+', map { /^0/ ? oct : /^\d/ ? $_ : ord } split while <>;