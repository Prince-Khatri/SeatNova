for i = 1, #KEYS do
    local status = redis.call('GET', KEYS[i])

    if status ~= 'AVAILABLE' then
        return 0
    end
end

for i = 1, #KEYS do
    redis.call('SET', KEYS[i], 'HOLD')
end

return 1
