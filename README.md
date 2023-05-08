# Sample Fulcro App

## Installation

Run the following in the project folder

```bash
# install js deps
npm install

# install clj deps
clj

# start shadow-cljs and then web interface at http://localhost:9630/ to make sure it's working
shadow-cljs start
```

## Start a REPL

Connect to the running shadow cljs repl at port 9000 (IDE specific). Type the following to switch into cljs mode:

```Clojure
(shadow/repl :main)
```

You should see output that looks like

```Clojure
[:selected :main]
```

Type any expression in the REPL to test connectivity.

```Clojure
(+ 1 1)
```

If the following message appears, reload your browser tab and try again:

```
No available JS runtime.
See https://shadow-cljs.github.io/docs/UsersGuide.html#repl-troubleshooting
```

Once the browser tab is reloaded, typing expressions in the REPL should work.
To test this we'll open a JS alert box:

```Clojure
(js/alert "HI!")
```

If you see an alert message in your browser, then you're connected!

