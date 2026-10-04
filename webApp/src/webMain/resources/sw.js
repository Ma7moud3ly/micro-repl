const CACHE = "micro-repl";

// serves the saved copy at once and refreshes it in the background
self.addEventListener("fetch", (event) => {
    const request = event.request;
    if (request.method !== "GET" || !request.url.startsWith(self.location.origin)) return;

    event.respondWith(
        caches.open(CACHE).then(async (cache) => {
            const saved = await cache.match(request);
            const fresh = fetch(request).then((response) => {
                if (response.ok) cache.put(request, response.clone());
                return response;
            });
            if (saved) {
                event.waitUntil(fresh.catch(() => {}));
                return saved;
            }
            return fresh;
        })
    );
});
