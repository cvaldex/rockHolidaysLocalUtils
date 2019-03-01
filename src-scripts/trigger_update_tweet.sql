CREATE TRIGGER update_tweet_trgr BEFORE UPDATE ON public.tweets FOR EACH ROW EXECUTE PROCEDURE update_tweet()
